package appuni.explore.domain;

import java.util.ArrayList;
import java.util.List;

public final class MarkerSpread {
    public static final double SEPARATION_METERS = 0.015;
    public static final double MAX_DISPLACEMENT_METERS = 0.02;
    public static final int PASSES = 8;

    private MarkerSpread() {
    }

    public static List<PlacedMarker> spread(List<PlacedMarker> cities) {
        if (cities.isEmpty()) {
            return List.of();
        }
        double[] originX = new double[cities.size()];
        double[] originZ = new double[cities.size()];
        double[] x = new double[cities.size()];
        double[] z = new double[cities.size()];
        for (int index = 0; index < cities.size(); index++) {
            originX[index] = cities.get(index).xMeters;
            originZ[index] = cities.get(index).zMeters;
            x[index] = originX[index];
            z[index] = originZ[index];
        }
        for (int pass = 0; pass < PASSES; pass++) {
            for (int i = 0; i < cities.size(); i++) {
                for (int j = i + 1; j < cities.size(); j++) {
                    separatePair(x, z, i, j);
                    clamp(x, z, originX, originZ, i);
                    clamp(x, z, originX, originZ, j);
                }
            }
        }
        List<PlacedMarker> placed = new ArrayList<>();
        for (int index = 0; index < cities.size(); index++) {
            PlacedMarker marker = cities.get(index);
            placed.add(new PlacedMarker(marker.partnerId, x[index], z[index], marker.usesStandIn));
        }
        return placed;
    }

    private static void separatePair(double[] x, double[] z, int i, int j) {
        double dx = x[j] - x[i];
        double dz = z[j] - z[i];
        double distance = Math.hypot(dx, dz);
        if (distance >= SEPARATION_METERS) {
            return;
        }
        if (distance < 1e-9) {
            dx = 1.0;
            dz = 0.0;
            distance = 1.0;
        }
        double current = Math.hypot(x[j] - x[i], z[j] - z[i]);
        double push = (SEPARATION_METERS - (current < 1e-9 ? 0.0 : current)) / 2.0;
        double unitX = dx / distance;
        double unitZ = dz / distance;
        x[i] -= unitX * push;
        z[i] -= unitZ * push;
        x[j] += unitX * push;
        z[j] += unitZ * push;
    }

    private static void clamp(double[] x, double[] z, double[] originX, double[] originZ, int index) {
        double dx = x[index] - originX[index];
        double dz = z[index] - originZ[index];
        double distance = Math.hypot(dx, dz);
        if (distance <= MAX_DISPLACEMENT_METERS) {
            return;
        }
        double scale = MAX_DISPLACEMENT_METERS / distance;
        x[index] = originX[index] + dx * scale;
        z[index] = originZ[index] + dz * scale;
    }
}
