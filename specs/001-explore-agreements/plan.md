# Implementation Plan: Explore University Agreements on a World Map

**Branch**: `001-explore-agreements` | **Date**: 2026-09-28 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-explore-agreements/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command; its definition describes the execution workflow.

## Summary

The MVP is an Android app that recognizes one predefined printed world map and places a partner-university logo at that partner's city. Tapping a logo shows the agreement details. The sample catalog and logos are bundled in the app. Opening a website is the only step that needs a network.

Tracking uses ARCore Augmented Images of that printed sheet. City latitude and longitude become meters in the image plane through a pure equirectangular conversion, then a pure nudge separates logos that would overlap. SceneView, used only inside the AR module, draws the logos and reports taps. Screens for permission, failure, and details stay outside that module and are Java views.

Application source is Java. The product behavior, module boundaries, and AR rules do not change. Tasks T001–T049 record the earlier Kotlin implementation. Tasks T050 onward replace that source with Java.

Decisions and rejected options are in [research.md](research.md).

## Technical Context

**Language/Version**: Java, JVM 17. Android `minSdk` 24 (Android 7.0). `targetSdk` is the current stable Android SDK when implementation starts.

**Primary Dependencies**: Java views and Material Components for screens. ARCore Augmented Images for recognition and pose. SceneView `arsceneview` for logo quads and taps, referenced only by the `ar` module. `org.json` for the asset catalog. Gradle Kotlin DSL may remain for build scripts; it is not application source.

**Storage**: Read-only app assets. `assets/catalog/partners.json`, `assets/catalog/logos/`, `assets/maps/world-map.png`, and `assets/maps/world-map.json`. No database and no download.

**Testing**: JUnit on the JVM for `:coordinates`, `:domain`, and `:data`. Device scenarios in [quickstart.md](quickstart.md) cover the camera.

**Target Platform**: Android phones. ARCore-capable phones run the map. Other phones get an explanation and no camera view.

**Project Type**: Mobile app. One Android project, several Gradle modules.

**Performance Goals**: Logos for cities in view appear within 3 seconds of the reference image reaching `TRACKING`. Marker pose updates with the camera frames while the image stays `TRACKING`. The sample catalog stays in memory. The expected size is tens of partners, not a live directory.

**Constraints**: Offline catalog, logos, and map. `https` websites open only with a validated network. No secrets in source. No second mode that shows the camera without anchors. The printed map width in the asset metadata must match the paper.

**Scale/Scope**: One host, one printed map, one agreement per partner, English sample content. Out of scope: editing, search away from the map, live catalogs, directions, other maps, codes, manual alignment, and a camera-only mode.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Before research | After design |
|------|-----------------|--------------|
| I. Android world-map AR | Pass. Android, one printed map, agreement markers. | Pass. Augmented Images of the bundled map. |
| II. Layered modules | Pass. UI, domain, data, and AR are separate Gradle modules. Coordinate conversion is its own module with no Android or camera dependency. | Pass. `app` renders states. `ar` is the only module that sees ARCore or SceneView. |
| III. Automated tests | Pass. Business rules and coordinate conversion are JVM tests. | Pass. Commands and cases are in [quickstart.md](quickstart.md). |
| IV. Graceful camera and tracking failure | Pass. Denial, loss, and incapable devices stay open with an explanation. | Pass. State contract in [contracts/experience-states.md](contracts/experience-states.md). |
| V. MVP simplicity | Pass. No backend, no extra product modes, equirectangular map authored for this app. | Pass. Assets instead of a database. SceneView avoids a custom renderer. Java views replace Compose without adding a product feature. |
| Security | Pass. No credentials. Websites are `https` view intents. | Pass. Catalog contains no secrets. |
| Quality gates | Pass. This plan names the module boundaries and the tests for rules and coordinates. | Pass. |

## Project Structure

### Documentation (this feature)

```text
specs/001-explore-agreements/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── partner-catalog.schema.json
│   └── experience-states.md
└── tasks.md             # Phase 2 (/speckit-tasks) — not created here
```

### Source Code (repository root)

```text
settings.gradle.kts
app/                         # Java views, navigation, permission UI
domain/                      # Partners, agreements, overlap, screen-state mapping
coordinates/                 # Latitude/longitude to image-local meters
data/                        # Asset catalog and map-frame loader
ar/                          # ARCore session, image database, anchors, SceneView
app/src/main/assets/catalog/partners.json
app/src/main/assets/catalog/logos/
app/src/main/assets/maps/world-map.png
app/src/main/assets/maps/world-map.json
```

**Structure Decision**: Five Gradle modules at the repository root. `domain` and `coordinates` are plain Java. `data` and `ar` are Android libraries. `app` depends on `domain`, `data`, and `ar`. `ar` depends on `domain` and `coordinates`. `data` depends on `domain`. `coordinates` depends on nothing in this project. That keeps geographic math runnable without a phone, and it keeps ARCore and SceneView out of the screens. Application and test sources live under `src/main/java` and `src/test/java`.

## Chosen technologies

| Need | Choice | Why |
|------|--------|-----|
| App UI | Java views, Material Components | Screens for permission, failure, and details. No AR types in these views. |
| Map recognition | ARCore Augmented Images, one database entry, physical width set | Recognizes the printed sheet itself and holds a pose while the phone moves. |
| Logo rendering and taps | SceneView inside `ar` | Textured quads and click callbacks without a custom GL stack. |
| City placement | Equirectangular conversion in `coordinates` | Testable offline mapping from latitude/longitude to the image plane. |
| Overlap | Iterative separation in `domain` | Logos are 1.5 cm across. Centers separate to 1.5 cm, and no marker moves more than 2 cm from its city. |
| Sample data | JSON assets parsed with `org.json` | Offline, no schema migration, no network catalog, no Kotlin compiler plugin. |
| Websites | `ACTION_VIEW` for `https` when a validated network exists | The one online action in the specification. |
| Incapable phones | AR optional, `ArCoreApk.checkAvailability()` | The app can open and explain. It does not start the camera. |

Rejected options are listed in [research.md](research.md).

## AR tracking

1. If availability is unsupported, install is declined, or the session fails, show the incapable-device explanation and do not create a session.
2. After the camera is granted, build an augmented-image database from `world-map.png` and `physicalWidthMeters`.
3. Disable plane finding. The only trackable that creates markers is this image.
4. While `TrackingState` is `TRACKING`, create or update one anchor per placed marker. The anchor pose is the image center pose composed with the marker's local `(x, small lift, z)` translation. The lift keeps the logo from z-fighting the paper.
5. `LAST_KNOWN_POSE` during `TRACKING` still shows markers. The sheet is fixed. A brief moment outside the camera frame does not count as losing the map.
6. On `PAUSED`, `STOPPED`, or a camera tracking failure, remove the logos and show the map-lost message. Aiming at the sheet until `TRACKING` returns restores the same cities.
7. Until the image is `TRACKING`, including a different sheet, a photo, or a code, stay in `SearchingForMap`, show no partner markers, and tell the person to aim at the predefined printed map.

The reference image must score at least 75 with `arcoreimg` before it is bundled. A border printed on that same sheet is allowed. Geographic bounds then use the content rectangle inside the border.

## Coordinate transformation

The converter does not import Android or ARCore. Inputs are a partner latitude/longitude and a `MapFrame`. Output is meters from the image center.

```text
u = contentLeft + (longitude - west) / (east - west) * (contentRight - contentLeft)
v = contentTop + (northLat - latitude) / (northLat - south) * (contentBottom - contentTop)
xMeters = (u - 0.5) * physicalWidthMeters
zMeters = (v - 0.5) * physicalHeightMeters
```

`u` grows east. `v` grows south, matching ARCore's image axis: +X to the right, +Z toward the bottom, origin at the center. North is the top of the authored image. Positions outside the geographic bounds are omitted.

`domain` then nudges those points. Logo diameter is 0.015 m (1.5 cm). If two centers are closer than 0.015 m, they move apart equally along the line between them until the centers are at least 0.015 m apart. Each point may move at most 0.02 m (2 cm) from its city. If both limits cannot be met, the 2 cm city limit wins. Eight passes cover clusters. The AR module consumes the nudged points and does not recompute geography.

## Main application flows

```text
start
  -> check ARCore availability
       incapable or install declined -> explanation, stop (no camera)
       available -> request camera
            denied once -> explanation and ask again
            will not ask again -> explain that camera permission must be enabled in system settings
            granted -> show camera and search for the printed map
                 tracking -> logos at nudged city positions
                      tap logo -> details
                           open https with network -> external viewer, which reports its own open failure
                           return from viewer -> same details
                           no network or no https -> message, details stay
                      Close or Back -> logos
                 tracking lost -> hide logos, ask the person to aim again
                 tracking returns -> same cities
```

The details surface lists name, city, country, logo or stand-in, agreement type, description, and website. The stand-in is the university name when the logo file is missing.

## Testing strategy

Automated, no camera:

- `coordinates`: west/east/north/south edges, image center, a southern city (positive Z), a point outside the frame, and a content rectangle that is inset for a border.
- `domain`: unique ids, one agreement, overlap separation, nudge clamp, missing website, stand-in flag, and the mapping from tracking status to `MapTracked` or `MapLost`.
- `data`: fixture JSON accepted by the catalog schema, and a broken fixture rejected.

Manual, on devices, as written in [quickstart.md](quickstart.md): recognition within 3 seconds, city alignment, movement, overlap taps, offline details, website with and without a network, tracking loss, camera denial, and an incapable phone.

## Technical risks and mitigations

| Risk | Mitigation |
|------|------------|
| A decorative world map has too few visual features, so ARCore never locks on. | Score the artwork with `arcoreimg` and require 75 or higher. Add a border on the same sheet if needed, and inset the geographic rectangle. |
| Printed width does not match `physicalWidthMeters`, so cities drift. | Print at the stored width. Quickstart checks a known city against the paper. |
| Equirectangular artwork is not what gets printed. | The asset and the paper are the same file. Another projection would be a change inside `coordinates` only. |
| `LAST_KNOWN_POSE` holds a bad pose after a fast move. | Hide logos when tracking leaves `TRACKING`. The fast-move acceptance case allows either a stable city or an explicit lost state, never a wrong city. |
| First ARCore availability check wants a network. | Treat an unresolved or failed check as the incapable-device explanation. Do not open the camera. After Play Services for AR is installed, the map and catalog stay offline. |
| SceneView's image API changes. | Pin the version. Keep it inside `ar`, behind the state contract, so screens do not move with it. |
| SceneView constructors are Kotlin types with default parameters. | Call them from Java inside `ar` and pass every argument explicitly. Do not move SceneView or ARCore into `app`. |
| `org.json` accepts JSON that the catalog schema would reject. | `CatalogParser` still applies the same required fields, ranges, and `https` rules the JVM tests already cover. |
| Many logos in one city still overlap after the 2 cm nudge limit. | Keep each marker within 2 cm of its city rather than jumping countries. Cover this pair in the JVM overlap test. |

## Complexity Tracking

No constitution violations. The extra `coordinates` module is the constitution's required camera-free conversion component, not an optional layer. SceneView replaces a custom renderer. Replacing Kotlin and Compose with Java views is a language migration, not a product feature.
