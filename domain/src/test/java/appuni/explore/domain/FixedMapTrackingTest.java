package appuni.explore.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FixedMapTrackingTest {
    @Test
    public void trackingWithLastKnownPoseStaysMapTracked() {
        TrackingMapResult result = TrackingStatusMapper.map(ImageTrack.TRACKING, PoseHold.LAST_KNOWN);
        assertEquals(ExperienceState.MapTracked.INSTANCE, result.state);
        assertTrue(result.markersVisible);
    }

    @Test
    public void leavingTrackingClearsMarkers() {
        TrackingMapResult result = TrackingStatusMapper.map(ImageTrack.STOPPED, PoseHold.LAST_KNOWN);
        assertEquals(ExperienceState.MapLost.INSTANCE, result.state);
        assertFalse(result.markersVisible);
    }
}
