package appuni.explore.ar;

import java.util.List;

public abstract class MapPhase {
    private MapPhase() {
    }

    public static final class Searching extends MapPhase {
        public static final Searching INSTANCE = new Searching();

        private Searching() {
        }
    }

    public static final class Tracked extends MapPhase {
        public final List<TrackedMarker> markers;

        public Tracked(List<TrackedMarker> markers) {
            this.markers = markers;
        }
    }

    public static final class Lost extends MapPhase {
        public static final Lost INSTANCE = new Lost();

        private Lost() {
        }
    }
}
