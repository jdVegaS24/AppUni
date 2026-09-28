package appuni.explore.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DeviceAccessPolicyTest {
    @Test
    public void incapableDeclinedOrFailedSessionKeepsTheCameraClosed() {
        DeviceAvailability[] values = {
                DeviceAvailability.INCAPABLE,
                DeviceAvailability.INSTALL_DECLINED,
                DeviceAvailability.SESSION_FAILED
        };
        for (DeviceAvailability availability : values) {
            AccessDecision decision = DeviceAccessPolicy.decide(availability, CameraAccess.GRANTED);
            assertEquals(ExperienceState.DeviceUnsupported.INSTANCE, decision.state);
            assertFalse(decision.cameraOpen);
        }
    }

    @Test
    public void cameraDenialMapsToNeedsCameraPermission() {
        AccessDecision decision = DeviceAccessPolicy.decide(DeviceAvailability.CAPABLE, CameraAccess.DENIED);
        assertEquals(new ExperienceState.NeedsCameraPermission(false), decision.state);
        assertFalse(decision.cameraOpen);
    }
}
