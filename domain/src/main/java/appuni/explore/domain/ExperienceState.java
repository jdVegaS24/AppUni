package appuni.explore.domain;

public abstract class ExperienceState {
    private ExperienceState() {
    }

    public static final class CheckingDevice extends ExperienceState {
        public static final CheckingDevice INSTANCE = new CheckingDevice();

        private CheckingDevice() {
        }
    }

    public static final class DeviceUnsupported extends ExperienceState {
        public static final DeviceUnsupported INSTANCE = new DeviceUnsupported();

        private DeviceUnsupported() {
        }
    }

    public static final class NeedsCameraPermission extends ExperienceState {
        public final boolean mustUseSystemSettings;

        public NeedsCameraPermission(boolean mustUseSystemSettings) {
            this.mustUseSystemSettings = mustUseSystemSettings;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof NeedsCameraPermission
                    && ((NeedsCameraPermission) other).mustUseSystemSettings == mustUseSystemSettings;
        }

        @Override
        public int hashCode() {
            return Boolean.hashCode(mustUseSystemSettings);
        }
    }

    public static final class SearchingForMap extends ExperienceState {
        public static final SearchingForMap INSTANCE = new SearchingForMap();

        private SearchingForMap() {
        }
    }

    public static final class MapTracked extends ExperienceState {
        public static final MapTracked INSTANCE = new MapTracked();

        private MapTracked() {
        }
    }

    public static final class MapLost extends ExperienceState {
        public static final MapLost INSTANCE = new MapLost();

        private MapLost() {
        }
    }

    public static final class DetailsOpen extends ExperienceState {
        public final String partnerId;

        public DetailsOpen(String partnerId) {
            this.partnerId = partnerId;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof DetailsOpen && ((DetailsOpen) other).partnerId.equals(partnerId);
        }

        @Override
        public int hashCode() {
            return partnerId.hashCode();
        }
    }
}
