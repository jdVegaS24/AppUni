package appuni.explore.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TrackingStatusTest {
    @Test
    public void trackingMapsToMapTracked() {
        TrackingMapResult result = TrackingStatusMapper.map(ImageTrack.TRACKING);
        assertEquals(ExperienceState.MapTracked.INSTANCE, result.state);
        assertTrue(result.markersVisible);
    }

    @Test
    public void pausedOrStoppedMapsToMapLostWithNoMarkers() {
        for (ImageTrack track : new ImageTrack[]{ImageTrack.PAUSED, ImageTrack.STOPPED}) {
            TrackingMapResult result = TrackingStatusMapper.map(track);
            assertEquals(ExperienceState.MapLost.INSTANCE, result.state);
            assertFalse(result.markersVisible);
        }
    }
}
