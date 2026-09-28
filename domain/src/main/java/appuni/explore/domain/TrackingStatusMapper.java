package appuni.explore.domain;

public final class TrackingStatusMapper {
    private TrackingStatusMapper() {
    }

    public static TrackingMapResult map(ImageTrack image) {
        return map(image, PoseHold.FULL);
    }

    public static TrackingMapResult map(ImageTrack image, PoseHold pose) {
        boolean stillTracked = image == ImageTrack.TRACKING
                && (pose == PoseHold.FULL || pose == PoseHold.LAST_KNOWN);
        if (stillTracked) {
            return new TrackingMapResult(ExperienceState.MapTracked.INSTANCE, true);
        }
        return new TrackingMapResult(ExperienceState.MapLost.INSTANCE, false);
    }
}
