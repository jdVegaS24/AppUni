package appuni.explore.domain;

public final class PlacedMarker {
    public final String partnerId;
    public final double xMeters;
    public final double zMeters;
    public final boolean usesStandIn;

    public PlacedMarker(String partnerId, double xMeters, double zMeters, boolean usesStandIn) {
        this.partnerId = partnerId;
        this.xMeters = xMeters;
        this.zMeters = zMeters;
        this.usesStandIn = usesStandIn;
    }
}
