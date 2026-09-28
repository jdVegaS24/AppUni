package appuni.explore.ui;

import appuni.explore.domain.CameraAccess;
import appuni.explore.domain.DeviceAccessPolicy;
import appuni.explore.domain.DeviceAvailability;
import appuni.explore.domain.ExperienceState;
import appuni.explore.domain.WebsiteAction;

public final class ExploreViewModel {
    public ExperienceState screen = ExperienceState.CheckingDevice.INSTANCE;
    public String selectedId;
    public String connectionMessage;
    public boolean requestedCamera;

    public void onAvailability(DeviceAvailability availability, boolean cameraGranted, boolean permanentDenial) {
        CameraAccess camera;
        if (cameraGranted) {
            camera = CameraAccess.GRANTED;
        } else if (permanentDenial) {
            camera = CameraAccess.PERMANENTLY_DENIED;
        } else {
            camera = CameraAccess.DENIED;
        }
        var decision = DeviceAccessPolicy.decide(availability, camera);
        if (!decision.cameraOpen) {
            selectedId = null;
        }
        screen = decision.state;
    }

    public void onMapPhase(ExperienceState phase) {
        if (phase == ExperienceState.DeviceUnsupported.INSTANCE) {
            selectedId = null;
            screen = phase;
            return;
        }
        if (phase == ExperienceState.MapLost.INSTANCE || phase == ExperienceState.SearchingForMap.INSTANCE) {
            if (phase == ExperienceState.MapLost.INSTANCE) {
                selectedId = null;
            }
            screen = phase;
            return;
        }
        if (selectedId != null && phase == ExperienceState.MapTracked.INSTANCE) {
            screen = new ExperienceState.DetailsOpen(selectedId);
        } else {
            screen = phase;
        }
    }

    public void onMarker(String partnerId) {
        selectedId = partnerId;
        connectionMessage = null;
        if (screen == ExperienceState.MapTracked.INSTANCE || screen instanceof ExperienceState.DetailsOpen) {
            screen = new ExperienceState.DetailsOpen(partnerId);
        }
    }

    public void closeDetails() {
        selectedId = null;
        connectionMessage = null;
        if (screen instanceof ExperienceState.DetailsOpen) {
            screen = ExperienceState.MapTracked.INSTANCE;
        }
    }

    public void onWebsite(WebsiteAction action) {
        if (action == WebsiteAction.ConnectionRequired.INSTANCE) {
            connectionMessage = "A connection is needed to open this website.";
        } else {
            connectionMessage = null;
        }
    }
}
