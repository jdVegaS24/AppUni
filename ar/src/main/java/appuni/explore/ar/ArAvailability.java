package appuni.explore.ar;

import android.app.Activity;

import appuni.explore.domain.DeviceAvailability;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.util.function.Consumer;

public final class ArAvailability {
    private final Activity activity;

    public ArAvailability(Activity activity) {
        this.activity = activity;
    }

    public void check(Consumer<DeviceAvailability> onResult) {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(activity);
        if (availability == ArCoreApk.Availability.UNKNOWN_CHECKING) {
            activity.getWindow().getDecorView().postDelayed(() -> check(onResult), 200);
        } else if (availability == ArCoreApk.Availability.SUPPORTED_INSTALLED) {
            onResult.accept(DeviceAvailability.CAPABLE);
        } else if (availability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD
                || availability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED) {
            requestInstall(onResult);
        } else {
            onResult.accept(DeviceAvailability.INCAPABLE);
        }
    }

    private void requestInstall(Consumer<DeviceAvailability> onResult) {
        try {
            ArCoreApk.InstallStatus status = ArCoreApk.getInstance().requestInstall(activity, true);
            if (status == ArCoreApk.InstallStatus.INSTALLED) {
                onResult.accept(DeviceAvailability.CAPABLE);
            } else if (status != ArCoreApk.InstallStatus.INSTALL_REQUESTED) {
                onResult.accept(DeviceAvailability.INCAPABLE);
            }
        } catch (UnavailableUserDeclinedInstallationException ignored) {
            onResult.accept(DeviceAvailability.INSTALL_DECLINED);
        } catch (Exception ignored) {
            onResult.accept(DeviceAvailability.INCAPABLE);
        }
    }
}
