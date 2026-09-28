package appuni.explore.ar;

import android.graphics.Bitmap;

import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Session;

public final class MapImageDatabase {
    private MapImageDatabase() {
    }

    public static void attach(Session session, Config config, Bitmap bitmap, String name, float widthMeters) {
        config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
        AugmentedImageDatabase database = new AugmentedImageDatabase(session);
        int index = database.addImage(name, bitmap, widthMeters);
        if (index < 0) {
            throw new IllegalStateException("The reference image was rejected");
        }
        config.setAugmentedImageDatabase(database);
    }
}
