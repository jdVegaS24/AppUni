package appuni.explore.coordinates;

public final class GeoFrame {
    public final double west;
    public final double east;
    public final double south;
    public final double northLat;
    public final double physicalWidthMeters;
    public final double physicalHeightMeters;
    public final double contentLeft;
    public final double contentTop;
    public final double contentRight;
    public final double contentBottom;

    public GeoFrame(
            double west,
            double east,
            double south,
            double northLat,
            double physicalWidthMeters,
            double physicalHeightMeters,
            double contentLeft,
            double contentTop,
            double contentRight,
            double contentBottom
    ) {
        this.west = west;
        this.east = east;
        this.south = south;
        this.northLat = northLat;
        this.physicalWidthMeters = physicalWidthMeters;
        this.physicalHeightMeters = physicalHeightMeters;
        this.contentLeft = contentLeft;
        this.contentTop = contentTop;
        this.contentRight = contentRight;
        this.contentBottom = contentBottom;
    }
}
