package appuni.explore.ar;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.ComponentActivity;

import appuni.explore.domain.ExperienceState;
import appuni.explore.domain.MapFrame;
import appuni.explore.domain.PartnerUniversity;

import com.google.ar.core.TrackingState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.android.filament.RenderableManager;

import io.github.sceneview.ar.ARSceneView;
import io.github.sceneview.ar.node.AnchorNode;
import io.github.sceneview.geometries.Plane;
import io.github.sceneview.node.ImageNode;
import io.github.sceneview.texture.ImageTexture;
import io.github.sceneview.texture.TextureSampler2D;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import dev.romainguy.kotlin.math.Float2;
import dev.romainguy.kotlin.math.Float3;

public final class MapArView {
    private MapArView() {
    }

    public static View create(
            Context context,
            List<PartnerUniversity> partners,
            MapFrame mapFrame,
            Bitmap referenceImage,
            Function<String, Boolean> logoExists,
            Function<String, Bitmap> logoFor,
            Consumer<ExperienceState> onPhase,
            Consumer<String> onMarkerTap
    ) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        ComponentActivity activity = findActivity(context);
        ARSceneView sceneView = new ARSceneView(context, null, 0, 0, null, activity.getLifecycle());
        sceneView.getPlaneRenderer().setEnabled(false);
        MapSessionController controller = new MapSessionController(
                partners,
                mapFrame,
                file -> file != null && Boolean.TRUE.equals(logoExists.apply(file))
        );
        Map<String, AnchorNode> nodes = new LinkedHashMap<>();
        sceneView.configureSession((session, config) -> {
            MapImageDatabase.attach(
                    session,
                    config,
                    referenceImage,
                    mapFrame.id,
                    (float) mapFrame.physicalWidthMeters
            );
            return Unit.INSTANCE;
        });
        sceneView.setOnSessionFailed(exception -> {
            mainHandler.post(() -> {
                controller.onSessionFailed();
                onPhase.accept(ExperienceState.DeviceUnsupported.INSTANCE);
            });
            return Unit.INSTANCE;
        });
        sceneView.setOnSessionUpdated((session, frame) -> {
            MapPhase phase = controller.onFrame(session, frame);
            if (phase instanceof MapPhase.Tracked) {
                syncMarkers(sceneView, logoFor, partners, ((MapPhase.Tracked) phase).markers, nodes, onMarkerTap, mainHandler);
            } else {
                for (AnchorNode node : nodes.values()) {
                    sceneView.removeChildNode(node);
                    node.destroy();
                }
                nodes.clear();
            }
            ExperienceState state = controller.experienceOf(phase);
            mainHandler.post(() -> onPhase.accept(state));
            return Unit.INSTANCE;
        });
        return sceneView;
    }

    private static void syncMarkers(
            ARSceneView sceneView,
            Function<String, Bitmap> logoFor,
            List<PartnerUniversity> partners,
            List<TrackedMarker> markers,
            Map<String, AnchorNode> nodes,
            Consumer<String> onMarkerTap,
            Handler mainHandler
    ) {
        List<String> ids = new java.util.ArrayList<>();
        for (TrackedMarker marker : markers) {
            ids.add(marker.partnerId);
        }
        List<String> stale = new java.util.ArrayList<>();
        for (String id : nodes.keySet()) {
            if (!ids.contains(id)) {
                stale.add(id);
            }
        }
        for (String id : stale) {
            AnchorNode node = nodes.remove(id);
            if (node != null) {
                sceneView.removeChildNode(node);
                node.destroy();
            }
        }
        for (TrackedMarker marker : markers) {
            if (nodes.containsKey(marker.partnerId)) {
                continue;
            }
            if (marker.anchor.getTrackingState() == TrackingState.STOPPED) {
                continue;
            }
            PartnerUniversity partner = null;
            for (PartnerUniversity candidate : partners) {
                if (candidate.id.equals(marker.partnerId)) {
                    partner = candidate;
                    break;
                }
            }
            if (partner == null) {
                continue;
            }
            Bitmap logo;
            if (marker.usesStandIn) {
                logo = standInBitmap(partner.name);
            } else {
                Bitmap loaded = logoFor.apply(partner.logoFile);
                logo = loaded != null ? loaded : standInBitmap(partner.name);
            }
            ImageNode imageNode = new ImageNode(
                    sceneView.getMaterialLoader(),
                    logo,
                    new Float3(0.015f, 0f, 0.015f),
                    Plane.Companion.getDEFAULT_CENTER(),
                    Plane.Companion.getDEFAULT_NORMAL(),
                    new Float2(1f, 1f),
                    ImageTexture.Companion.getDEFAULT_TYPE(),
                    new TextureSampler2D(),
                    (Function1<RenderableManager.Builder, Unit>) builder -> Unit.INSTANCE,
                    (Function1<ImageTexture.Builder, Unit>) builder -> Unit.INSTANCE
            );
            imageNode.setOnSingleTapConfirmed(motionEvent -> {
                mainHandler.post(() -> onMarkerTap.accept(marker.partnerId));
                return true;
            });
            AnchorNode anchorNode = new AnchorNode(sceneView.getEngine(), marker.anchor, null, null, null, null);
            anchorNode.setPositionEditable(false);
            anchorNode.addChildNode(imageNode);
            sceneView.addChildNode(anchorNode);
            nodes.put(marker.partnerId, anchorNode);
        }
    }

    private static Bitmap standInBitmap(String name) {
        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor("#1B4F72"));
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(28f);
        String[] words = name.split(" ");
        for (int index = 0; index < words.length; index++) {
            canvas.drawText(words[index], 128f, 110f + index * 34f, paint);
        }
        return bitmap;
    }

    private static ComponentActivity findActivity(Context context) {
        Context current = context;
        while (current instanceof ContextWrapper) {
            if (current instanceof ComponentActivity) {
                return (ComponentActivity) current;
            }
            current = ((ContextWrapper) current).getBaseContext();
        }
        if (current instanceof ComponentActivity) {
            return (ComponentActivity) current;
        }
        throw new IllegalStateException("Camera view is not hosted by an activity");
    }
}
