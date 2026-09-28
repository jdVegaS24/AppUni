package appuni.explore.domain;

public final class MapFrame {
    public final String id;
    public final String imageFile;
    public final double physicalWidthMeters;
    public final int imagePixelWidth;
    public final int imagePixelHeight;
    public final String projection;
    public final String north;
    public final double west;
    public final double east;
    public final double south;
    public final double northLat;
    public final double contentLeft;
    public final double contentTop;
    public final double contentRight;
    public final double contentBottom;

    public MapFrame(
            String id,
            String imageFile,
            double physicalWidthMeters,
            int imagePixelWidth,
            int imagePixelHeight,
            String projection,
            String north,
            double west,
            double east,
            double south,
            double northLat,
            double contentLeft,
            double contentTop,
            double contentRight,
            double contentBottom
    ) {
        if (isBlank(id)) {
            throw new IllegalArgumentException("Map id is required");
        }
        if (isBlank(imageFile)) {
            throw new IllegalArgumentException("Map image is required");
        }
        if (!(physicalWidthMeters > 0.0)) {
            throw new IllegalArgumentException("Physical width must be greater than 0");
        }
        if (!(imagePixelWidth > 0 && imagePixelHeight > 0)) {
            throw new IllegalArgumentException("Pixel size must be greater than 0");
        }
        if (isBlank(projection)) {
            throw new IllegalArgumentException("Projection is required");
        }
        if (isBlank(north)) {
            throw new IllegalArgumentException("North is required");
        }
        if (!(east > west)) {
            throw new IllegalArgumentException("East must be greater than west");
        }
        if (!(northLat > south)) {
            throw new IllegalArgumentException("North latitude must be greater than south");
        }
        if (!(contentRight > contentLeft && contentBottom > contentTop)) {
            throw new IllegalArgumentException(
                    "Content rectangle must have right greater than left and bottom greater than top"
            );
        }
        this.id = id;
        this.imageFile = imageFile;
        this.physicalWidthMeters = physicalWidthMeters;
        this.imagePixelWidth = imagePixelWidth;
        this.imagePixelHeight = imagePixelHeight;
        this.projection = projection;
        this.north = north;
        this.west = west;
        this.east = east;
        this.south = south;
        this.northLat = northLat;
        this.contentLeft = contentLeft;
        this.contentTop = contentTop;
        this.contentRight = contentRight;
        this.contentBottom = contentBottom;
    }

    public double physicalHeightMeters() {
        return physicalWidthMeters * (double) imagePixelHeight / (double) imagePixelWidth;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
