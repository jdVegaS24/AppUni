package appuni.explore.ar;

import appuni.explore.coordinates.GeoFrame;
import appuni.explore.coordinates.GeoToImage;
import appuni.explore.coordinates.ImageMeters;
import appuni.explore.domain.ExperienceState;
import appuni.explore.domain.ImageTrack;
import appuni.explore.domain.MapFrame;
import appuni.explore.domain.MarkerSpread;
import appuni.explore.domain.PartnerUniversity;
import appuni.explore.domain.PlacedMarker;
import appuni.explore.domain.PoseHold;
import appuni.explore.domain.TrackingStatusMapper;

import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public final class MapSessionController {
    private final MapFrame mapFrame;
    private final MarkerAnchorPlanner planner;
    private final List<PlacedMarker> placedMarkers;
    private boolean hadTracking;

    public MapSessionController(List<PartnerUniversity> partners, MapFrame mapFrame, Predicate<String> logoAvailable) {
        this(partners, mapFrame, logoAvailable, new MarkerAnchorPlanner());
    }

    public MapSessionController(
            List<PartnerUniversity> partners,
            MapFrame mapFrame,
            Predicate<String> logoAvailable,
            MarkerAnchorPlanner planner
    ) {
        this.mapFrame = mapFrame;
        this.planner = planner;
        this.placedMarkers = place(partners, mapFrame, logoAvailable);
    }

    public MapPhase onFrame(Session session, Frame frame) {
        if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            planner.detachAll();
            return lostOrSearching();
        }
        AugmentedImage image = null;
        Collection<AugmentedImage> images = session.getAllTrackables(AugmentedImage.class);
        for (AugmentedImage candidate : images) {
            if (mapFrame.id.equals(candidate.getName())) {
                image = candidate;
                break;
            }
        }
        ImageTrack track;
        if (image == null) {
            track = null;
        } else if (image.getTrackingState() == TrackingState.TRACKING) {
            track = ImageTrack.TRACKING;
        } else if (image.getTrackingState() == TrackingState.PAUSED) {
            track = ImageTrack.PAUSED;
        } else if (image.getTrackingState() == TrackingState.STOPPED) {
            track = ImageTrack.STOPPED;
        } else {
            track = null;
        }
        if (track == null) {
            planner.detachAll();
            return lostOrSearching();
        }
        PoseHold pose = image.getTrackingMethod() == AugmentedImage.TrackingMethod.LAST_KNOWN_POSE
                ? PoseHold.LAST_KNOWN
                : PoseHold.FULL;
        if (!TrackingStatusMapper.map(track, pose).markersVisible) {
            planner.detachAll();
            return lostOrSearching();
        }
        hadTracking = true;
        return new MapPhase.Tracked(planner.sync(image, placedMarkers, frame.getCamera(), true));
    }

    public void onSessionFailed() {
        planner.detachAll();
        hadTracking = false;
    }

    public ExperienceState experienceOf(MapPhase phase) {
        if (phase instanceof MapPhase.Searching) {
            return ExperienceState.SearchingForMap.INSTANCE;
        }
        if (phase instanceof MapPhase.Lost) {
            return ExperienceState.MapLost.INSTANCE;
        }
        return ExperienceState.MapTracked.INSTANCE;
    }

    private MapPhase lostOrSearching() {
        return hadTracking ? MapPhase.Lost.INSTANCE : MapPhase.Searching.INSTANCE;
    }

    private static List<PlacedMarker> place(List<PartnerUniversity> partners, MapFrame frame, Predicate<String> logoAvailable) {
        GeoFrame geo = new GeoFrame(
                frame.west,
                frame.east,
                frame.south,
                frame.northLat,
                frame.physicalWidthMeters,
                frame.physicalHeightMeters(),
                frame.contentLeft,
                frame.contentTop,
                frame.contentRight,
                frame.contentBottom
        );
        List<PlacedMarker> cities = new ArrayList<>();
        for (PartnerUniversity partner : partners) {
            ImageMeters point = GeoToImage.toImage(partner.latitude, partner.longitude, geo);
            if (point == null) {
                continue;
            }
            boolean blankLogo = partner.logoFile == null || partner.logoFile.trim().isEmpty();
            boolean usesStandIn = blankLogo || !logoAvailable.test(partner.logoFile);
            cities.add(new PlacedMarker(partner.id, point.xMeters, point.zMeters, usesStandIn));
        }
        return MarkerSpread.spread(cities);
    }
}
