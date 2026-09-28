package appuni.explore.coordinates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GeoToImageTest {
    private final GeoFrame full = new GeoFrame(-180.0, 180.0, -90.0, 90.0, 0.40, 0.20, 0.0, 0.0, 1.0, 1.0);

    @Test
    public void westEdgeIsLeftOfCenter() {
        ImageMeters point = GeoToImage.toImage(0.0, -180.0, full);
        assertEquals(-0.20, point.xMeters, 1e-9);
        assertEquals(0.0, point.zMeters, 1e-9);
    }

    @Test
    public void eastEdgeIsRightOfCenter() {
        ImageMeters point = GeoToImage.toImage(0.0, 180.0, full);
        assertEquals(0.20, point.xMeters, 1e-9);
        assertEquals(0.0, point.zMeters, 1e-9);
    }

    @Test
    public void northEdgeIsTowardTheTop() {
        ImageMeters point = GeoToImage.toImage(90.0, 0.0, full);
        assertEquals(0.0, point.xMeters, 1e-9);
        assertEquals(-0.10, point.zMeters, 1e-9);
    }

    @Test
    public void southEdgeIsTowardTheBottom() {
        ImageMeters point = GeoToImage.toImage(-90.0, 0.0, full);
        assertEquals(0.0, point.xMeters, 1e-9);
        assertEquals(0.10, point.zMeters, 1e-9);
    }

    @Test
    public void imageCenterIsTheOrigin() {
        ImageMeters point = GeoToImage.toImage(0.0, 0.0, full);
        assertEquals(0.0, point.xMeters, 1e-9);
        assertEquals(0.0, point.zMeters, 1e-9);
    }

    @Test
    public void southernCityHasPositiveZ() {
        ImageMeters point = GeoToImage.toImage(-45.0, 0.0, full);
        assertTrue(point.zMeters > 0.0);
    }

    @Test
    public void pointOutsideTheFrameIsOmitted() {
        assertNull(GeoToImage.toImage(0.0, 181.0, full));
        assertNull(GeoToImage.toImage(-90.1, 0.0, full));
    }

    @Test
    public void insetContentRectangleShiftsTheWestEdge() {
        GeoFrame inset = new GeoFrame(-180.0, 180.0, -90.0, 90.0, 0.40, 0.20, 0.1, 0.2, 0.9, 0.8);
        ImageMeters point = GeoToImage.toImage(0.0, -180.0, inset);
        assertEquals((0.1 - 0.5) * 0.40, point.xMeters, 1e-9);
        assertEquals(0.0, point.zMeters, 1e-9);
    }
}
