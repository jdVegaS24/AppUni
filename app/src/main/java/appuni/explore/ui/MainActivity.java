package appuni.explore.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.io.InputStream;
import java.util.List;

import appuni.explore.R;
import appuni.explore.ar.ArAvailability;
import appuni.explore.ar.MapArView;
import appuni.explore.data.AssetCatalogSource;
import appuni.explore.domain.Catalog;
import appuni.explore.domain.CatalogRules;
import appuni.explore.domain.DeviceAvailability;
import appuni.explore.domain.ExperienceState;
import appuni.explore.domain.MapFrame;
import appuni.explore.domain.PartnerUniversity;

public class MainActivity extends ComponentActivity {
    private final ExploreViewModel model = new ExploreViewModel();
    private ActivityResultLauncher<String> permissionLauncher;
    private AssetCatalogSource source;
    private Catalog catalog;
    private MapFrame mapFrame;
    private Bitmap referenceImage;
    private boolean arAttached;
    private OnBackPressedCallback detailsBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    boolean permanent = !granted
                            && model.requestedCamera
                            && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                    model.onAvailability(DeviceAvailability.CAPABLE, granted, permanent);
                    render();
                }
        );
        source = new AssetCatalogSource(getAssets());
        try {
            catalog = source.catalog();
            mapFrame = source.mapFrame();
            referenceImage = BitmapFactory.decodeStream(getAssets().open(mapFrame.imageFile));
        } catch (Exception error) {
            throw new IllegalStateException("The agreement catalog could not be read", error);
        }
        setContentView(R.layout.activity_main);
        detailsBack = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                model.closeDetails();
                render();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, detailsBack);
        findViewById(R.id.closeDetails).setOnClickListener(view -> {
            model.closeDetails();
            render();
        });
        findViewById(R.id.openWebsite).setOnClickListener(view -> {
            PartnerUniversity partner = selectedPartner();
            if (partner == null) {
                return;
            }
            model.onWebsite(new WebsiteOpener(this).open(partner.website));
            render();
        });
        findViewById(R.id.gateAction).setOnClickListener(view -> {
            if (model.screen instanceof ExperienceState.NeedsCameraPermission
                    && ((ExperienceState.NeedsCameraPermission) model.screen).mustUseSystemSettings) {
                openAppSettings();
            } else {
                requestCamera();
            }
        });
        render();
        refreshAvailability();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (model.screen instanceof ExperienceState.NeedsCameraPermission
                || model.screen == ExperienceState.CheckingDevice.INSTANCE) {
            boolean permanent = model.screen instanceof ExperienceState.NeedsCameraPermission
                    && ((ExperienceState.NeedsCameraPermission) model.screen).mustUseSystemSettings;
            new ArAvailability(this).check(availability -> {
                if (availability == DeviceAvailability.CHECKING) {
                    return;
                }
                boolean granted = cameraGranted();
                model.onAvailability(availability, granted, permanent && !granted);
                render();
            });
        }
    }

    private void refreshAvailability() {
        new ArAvailability(this).check(availability -> {
            if (availability == DeviceAvailability.CHECKING) {
                return;
            }
            model.onAvailability(availability, cameraGranted(), false);
            render();
        });
    }

    private void requestCamera() {
        model.requestedCamera = true;
        permissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    private boolean cameraGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void render() {
        View gate = findViewById(R.id.gateScreen);
        View map = findViewById(R.id.mapScreen);
        boolean mapVisible = model.screen != ExperienceState.CheckingDevice.INSTANCE
                && model.screen != ExperienceState.DeviceUnsupported.INSTANCE
                && !(model.screen instanceof ExperienceState.NeedsCameraPermission);
        gate.setVisibility(mapVisible ? View.GONE : View.VISIBLE);
        map.setVisibility(mapVisible ? View.VISIBLE : View.GONE);
        if (!mapVisible) {
            bindGate();
            detailsBack.setEnabled(false);
            return;
        }
        attachArOnce();
        bindMap();
    }

    private void bindGate() {
        TextView message = findViewById(R.id.gateMessage);
        MaterialButton action = findViewById(R.id.gateAction);
        if (model.screen == ExperienceState.DeviceUnsupported.INSTANCE) {
            message.setText("This phone cannot run the map experience.");
            action.setVisibility(View.GONE);
        } else if (model.screen instanceof ExperienceState.NeedsCameraPermission) {
            boolean settings = ((ExperienceState.NeedsCameraPermission) model.screen).mustUseSystemSettings;
            message.setText(settings
                    ? "Camera permission must be enabled in the application's system settings."
                    : "The camera is needed to view the printed map. You can allow it and try again.");
            action.setVisibility(View.VISIBLE);
            action.setText(settings ? "Open settings" : "Try again");
        } else {
            message.setText("Checking whether this phone can run the map experience.");
            action.setVisibility(View.GONE);
        }
    }

    private void attachArOnce() {
        if (arAttached) {
            return;
        }
        FrameLayout host = findViewById(R.id.arHost);
        host.addView(MapArView.create(
                this,
                catalog.partners,
                mapFrame,
                referenceImage,
                this::logoExists,
                this::logoFor,
                phase -> {
                    model.onMapPhase(phase);
                    render();
                },
                partnerId -> {
                    model.onMarker(partnerId);
                    render();
                }
        ), new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        arAttached = true;
    }

    private void bindMap() {
        TextView status = findViewById(R.id.statusMessage);
        if (model.screen == ExperienceState.SearchingForMap.INSTANCE) {
            status.setVisibility(View.VISIBLE);
            status.setText("Aim at the predefined printed world map.");
        } else if (model.screen == ExperienceState.MapLost.INSTANCE) {
            status.setVisibility(View.VISIBLE);
            status.setText("The map was lost. Aim at the printed map again.");
        } else {
            status.setVisibility(View.GONE);
        }
        PartnerUniversity partner = selectedPartner();
        boolean detailsOpen = partner != null
                && model.screen != ExperienceState.MapLost.INSTANCE
                && model.screen != ExperienceState.SearchingForMap.INSTANCE
                && model.screen != ExperienceState.DeviceUnsupported.INSTANCE;
        View sheet = findViewById(R.id.detailsSheet);
        sheet.setVisibility(detailsOpen ? View.VISIBLE : View.GONE);
        detailsBack.setEnabled(detailsOpen);
        if (!detailsOpen || partner == null) {
            return;
        }
        bindDetails(partner);
    }

    private void bindDetails(PartnerUniversity partner) {
        ImageView logoView = findViewById(R.id.detailLogo);
        TextView standIn = findViewById(R.id.detailStandIn);
        Bitmap logo = partner.logoFile == null ? null : logoFor(partner.logoFile);
        if (logo != null) {
            logoView.setVisibility(View.VISIBLE);
            logoView.setImageBitmap(logo);
            logoView.setContentDescription(partner.name);
            standIn.setVisibility(View.GONE);
        } else {
            logoView.setVisibility(View.GONE);
            standIn.setVisibility(View.VISIBLE);
            standIn.setText(partner.name);
        }
        ((TextView) findViewById(R.id.detailName)).setText(partner.name);
        ((TextView) findViewById(R.id.detailCity)).setText(partner.city);
        ((TextView) findViewById(R.id.detailCountry)).setText(partner.country);
        ((TextView) findViewById(R.id.detailType)).setText(partner.agreement.type);
        ((TextView) findViewById(R.id.detailDescription)).setText(partner.agreement.description);
        TextView website = findViewById(R.id.detailWebsite);
        MaterialButton open = findViewById(R.id.openWebsite);
        if (CatalogRules.isWebsiteOpenable(partner.website)) {
            website.setText(partner.website);
            open.setVisibility(View.VISIBLE);
        } else {
            website.setText("Website unavailable");
            open.setVisibility(View.GONE);
        }
        TextView connection = findViewById(R.id.connectionMessage);
        if (model.connectionMessage == null) {
            connection.setVisibility(View.GONE);
        } else {
            connection.setVisibility(View.VISIBLE);
            connection.setText(model.connectionMessage);
        }
    }

    private PartnerUniversity selectedPartner() {
        if (model.selectedId == null) {
            return null;
        }
        List<PartnerUniversity> partners = catalog.partners;
        for (PartnerUniversity partner : partners) {
            if (partner.id.equals(model.selectedId)) {
                return partner;
            }
        }
        return null;
    }

    private boolean logoExists(String file) {
        InputStream stream = source.openLogo(file);
        if (stream == null) {
            return false;
        }
        try {
            stream.close();
        } catch (Exception ignored) {
            return true;
        }
        return true;
    }

    private Bitmap logoFor(String file) {
        InputStream stream = source.openLogo(file);
        if (stream == null) {
            return null;
        }
        try (InputStream logo = stream) {
            return BitmapFactory.decodeStream(logo);
        } catch (Exception ignored) {
            return null;
        }
    }
}
