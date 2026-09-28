package appuni.explore.ar;

import appuni.explore.domain.PlacedMarker;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Camera;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MarkerAnchorPlanner {
    private static final float LOGO_LIFT_METERS = 0.005f;
    private final Map<String, Anchor> anchors = new LinkedHashMap<>();

    public List<TrackedMarker> sync(AugmentedImage image, List<PlacedMarker> markers, Camera camera, boolean showMarkers) {
        if (!showMarkers || image == null || image.getTrackingState() != TrackingState.TRACKING) {
            detachAll();
            return List.of();
        }
        List<PlacedMarker> visible = new ArrayList<>();
        for (PlacedMarker marker : markers) {
            Pose world = image.getCenterPose().compose(localPose(marker));
            if (cityInView(camera, world)) {
                visible.add(marker);
            }
        }
        List<String> visibleIds = new ArrayList<>();
        for (PlacedMarker marker : visible) {
            visibleIds.add(marker.partnerId);
        }
        List<String> stale = new ArrayList<>();
        for (String id : anchors.keySet()) {
            if (!visibleIds.contains(id)) {
                stale.add(id);
            }
        }
        for (String id : stale) {
            Anchor removed = anchors.remove(id);
            if (removed != null) {
                removed.detach();
            }
        }
        List<TrackedMarker> tracked = new ArrayList<>();
        for (PlacedMarker marker : visible) {
            Anchor existing = anchors.get(marker.partnerId);
            Anchor anchor;
            if (existing == null || existing.getTrackingState() == TrackingState.STOPPED) {
                if (existing != null) {
                    existing.detach();
                }
                anchor = image.createAnchor(localPose(marker));
                anchors.put(marker.partnerId, anchor);
            } else {
                anchor = existing;
            }
            tracked.add(new TrackedMarker(marker.partnerId, anchor, marker.usesStandIn));
        }
        return tracked;
    }

    public void detachAll() {
        for (Anchor anchor : anchors.values()) {
            anchor.detach();
        }
        anchors.clear();
    }

    private static Pose localPose(PlacedMarker marker) {
        return Pose.makeTranslation((float) marker.xMeters, LOGO_LIFT_METERS, (float) marker.zMeters);
    }

    private static boolean cityInView(Camera camera, Pose worldPose) {
        float[] world = worldPose.transformPoint(new float[]{0f, 0f, 0f});
        float[] view = new float[16];
        float[] projection = new float[16];
        camera.getViewMatrix(view, 0);
        camera.getProjectionMatrix(projection, 0, 0.05f, 50f);
        float[] eye = multiply(view, world[0], world[1], world[2], 1f);
        if (eye[2] >= 0f) {
            return false;
        }
        float[] clip = multiply(projection, eye[0], eye[1], eye[2], eye[3]);
        if (clip[3] == 0f) {
            return false;
        }
        float ndcX = clip[0] / clip[3];
        float ndcY = clip[1] / clip[3];
        return ndcX >= -1f && ndcX <= 1f && ndcY >= -1f && ndcY <= 1f;
    }

    private static float[] multiply(float[] matrix, float x, float y, float z, float w) {
        return new float[]{
                matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12] * w,
                matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13] * w,
                matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14] * w,
                matrix[3] * x + matrix[7] * y + matrix[11] * z + matrix[15] * w
        };
    }
}
