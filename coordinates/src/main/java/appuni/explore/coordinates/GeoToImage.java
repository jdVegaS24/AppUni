package appuni.explore.coordinates;

public final class GeoToImage {
    private GeoToImage() {
    }

    public static ImageMeters toImage(double latitude, double longitude, GeoFrame frame) {
        if (longitude < frame.west || longitude > frame.east) {
            return null;
        }
        if (latitude < frame.south || latitude > frame.northLat) {
            return null;
        }
        double u = frame.contentLeft
                + (longitude - frame.west) / (frame.east - frame.west) * (frame.contentRight - frame.contentLeft);
        double v = frame.contentTop
                + (frame.northLat - latitude) / (frame.northLat - frame.south) * (frame.contentBottom - frame.contentTop);
        if (u < 0.0 || u > 1.0 || v < 0.0 || v > 1.0) {
            return null;
        }
        return new ImageMeters(
                (u - 0.5) * frame.physicalWidthMeters,
                (v - 0.5) * frame.physicalHeightMeters
        );
    }
}
