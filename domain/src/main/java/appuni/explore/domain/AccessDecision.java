package appuni.explore.domain;

public final class AccessDecision {
    public final ExperienceState state;
    public final boolean cameraOpen;

    public AccessDecision(ExperienceState state, boolean cameraOpen) {
        this.state = state;
        this.cameraOpen = cameraOpen;
    }
}
