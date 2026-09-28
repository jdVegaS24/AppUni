package appuni.explore.ar;

import com.google.ar.core.Anchor;

public final class TrackedMarker {
    public final String partnerId;
    public final Anchor anchor;
    public final boolean usesStandIn;

    public TrackedMarker(String partnerId, Anchor anchor, boolean usesStandIn) {
        this.partnerId = partnerId;
        this.anchor = anchor;
        this.usesStandIn = usesStandIn;
    }
}
