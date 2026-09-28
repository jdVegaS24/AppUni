package appuni.explore.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MarkerSpreadTest {
    @Test
    public void closeCentersMoveApartEquallyUntilSeparated() {
        List<PlacedMarker> placed = MarkerSpread.spread(List.of(
                marker("a", 0.0, 0.0),
                marker("b", 0.004, 0.0)
        ));
        PlacedMarker a = find(placed, "a");
        PlacedMarker b = find(placed, "b");
        assertEquals(0.0055, Math.abs(a.xMeters - 0.0), 1e-4);
        assertEquals(0.0055, Math.abs(b.xMeters - 0.004), 1e-4);
        assertTrue(distance(a, b) >= 0.015 - 1e-6);
    }

    @Test
    public void noMarkerMovesMoreThanTwoCentimeters() {
        List<PlacedMarker> placed = MarkerSpread.spread(cluster(9));
        for (PlacedMarker marker : placed) {
            assertTrue(Math.hypot(marker.xMeters, marker.zMeters) <= 0.02 + 1e-6);
        }
    }

    @Test
    public void cityLimitWinsWhenSeparationCannotBeMet() {
        List<PlacedMarker> placed = MarkerSpread.spread(cluster(9));
        double closest = Double.POSITIVE_INFINITY;
        for (PlacedMarker left : placed) {
            for (PlacedMarker right : placed) {
                if (!left.partnerId.equals(right.partnerId)) {
                    closest = Math.min(closest, distance(left, right));
                }
            }
        }
        assertTrue(closest < 0.015);
        for (PlacedMarker marker : placed) {
            assertTrue(Math.hypot(marker.xMeters, marker.zMeters) <= 0.02 + 1e-6);
        }
    }

    @Test
    public void partnersAreNotCollapsedIntoOneMarker() {
        List<PlacedMarker> placed = MarkerSpread.spread(cluster(9));
        Set<String> ids = new HashSet<>();
        for (PlacedMarker marker : placed) {
            ids.add(marker.partnerId);
        }
        assertEquals(9, placed.size());
        assertEquals(9, ids.size());
    }

    private static PlacedMarker marker(String id, double x, double z) {
        return new PlacedMarker(id, x, z, false);
    }

    private static List<PlacedMarker> cluster(int count) {
        List<PlacedMarker> markers = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            markers.add(marker("p" + index, 0.0, 0.0));
        }
        return markers;
    }

    private static PlacedMarker find(List<PlacedMarker> placed, String id) {
        for (PlacedMarker marker : placed) {
            if (marker.partnerId.equals(id)) {
                return marker;
            }
        }
        throw new IllegalStateException(id);
    }

    private static double distance(PlacedMarker left, PlacedMarker right) {
        return Math.hypot(left.xMeters - right.xMeters, left.zMeters - right.zMeters);
    }
}
