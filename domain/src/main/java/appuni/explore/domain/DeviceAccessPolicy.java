package appuni.explore.domain;

public final class DeviceAccessPolicy {
    private DeviceAccessPolicy() {
    }

    public static AccessDecision decide(DeviceAvailability device, CameraAccess camera) {
        switch (device) {
            case CHECKING:
                return new AccessDecision(ExperienceState.CheckingDevice.INSTANCE, false);
            case INCAPABLE:
            case INSTALL_DECLINED:
            case SESSION_FAILED:
                return new AccessDecision(ExperienceState.DeviceUnsupported.INSTANCE, false);
            case CAPABLE:
                switch (camera) {
                    case GRANTED:
                        return new AccessDecision(ExperienceState.SearchingForMap.INSTANCE, true);
                    case DENIED:
                        return new AccessDecision(new ExperienceState.NeedsCameraPermission(false), false);
                    case PERMANENTLY_DENIED:
                        return new AccessDecision(new ExperienceState.NeedsCameraPermission(true), false);
                    default:
                        throw new IllegalArgumentException("Unknown camera access");
                }
            default:
                throw new IllegalArgumentException("Unknown device availability");
        }
    }
}
