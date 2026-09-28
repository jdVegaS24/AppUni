package appuni.explore.domain;

public final class TrackingMapResult {
    public final ExperienceState state;
    public final boolean markersVisible;

    public TrackingMapResult(ExperienceState state, boolean markersVisible) {
        this.state = state;
        this.markersVisible = markersVisible;
    }
}
