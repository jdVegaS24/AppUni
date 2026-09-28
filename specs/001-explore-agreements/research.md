# Research: Explore University Agreements on a World Map

## AR framework

- **Decision**: Track the map with ARCore Augmented Images. Render logo markers with SceneView (`io.github.sceneview:arsceneview`) inside the `ar` module only.
- **Rationale**: Augmented Images is the Android mechanism that recognizes one known printed image and returns a pose while the phone moves. SceneView supplies image anchors, textured quads, and tap handling so the MVP does not include a hand-written OpenGL renderer. The app UI never calls ARCore.
- **Alternatives considered**: Unity AR Foundation adds a second runtime and fights the UI/domain/data/AR split. Vuforia adds a commercial image-target SDK the MVP does not need. A custom feature matcher is more code and a weaker pose. Raw ARCore with a custom GL renderer is viable but spends the MVP on rendering instead of placement and failure behavior.

## Recognition of the printed map

- **Decision**: Ship one reference image of the world map in app assets and register it in an ARCore augmented-image database with its real printed width in meters. The phone recognizes that image. There is no code, no extra fiducial, and no manual alignment step.
- **Rationale**: The specification requires the printed map itself to be the tracked object. Giving ARCore the physical width makes the initial pose usable immediately. Google's guidance is to supply size for images larger than about 75 cm, which a tabletop world map is.
- **Alternatives considered**: Estimating size at runtime leaves the first moments in a paused pose, which misses the "markers within 3 seconds" outcome. QR codes and printed guide marks were rejected in clarification.

## Anchoring

- **Decision**: Treat the map as a fixed image. Show markers only while `TrackingState` is `TRACKING`. Ignore `TrackingMethod` for show/hide. Attach each marker to an anchor posed in the image's local frame. Hide every marker when the image is `PAUSED` or `STOPPED`, or when camera tracking fails, and tell the person the map was lost.
- **Rationale**: For a sheet that does not move, ARCore still reports a valid pose under `LAST_KNOWN_POSE` while the phone moves and the map is briefly out of frame. That is what keeps cities fixed on the paper. A paused or stopped image must not keep logos on screen, because those poses are no longer trustworthy.
- **Alternatives considered**: Hiding markers on `LAST_KNOWN_POSE` would make logos vanish whenever the map is not fully in frame, which breaks moving the phone around the sheet. Leaving markers visible after `STOPPED` can park them on the wrong place.

## Geographic projection

- **Decision**: Author the reference map as a north-up equirectangular image and store its longitude/latitude bounds plus an optional content rectangle. Convert city coordinates with linear interpolation inside that rectangle. Output meters in the image plane: +X to the right, +Z toward the bottom of the image, origin at the image center.
- **Rationale**: The official Augmented Image pose uses that axis pair (+Y out of the page). A linear equirectangular mapping is a pure function with no network and no device, which is what the constitution requires for coordinate tests. The MVP controls the printed artwork, so it does not have to reverse an unknown commercial projection.
- **Alternatives considered**: Web Mercator is a different formula for the same module and is unnecessary if we print the reference image ourselves. Geocoding APIs need a network and violate offline exploration. Placing a marker anywhere inside the country was rejected in clarification.

## Overlap

- **Decision**: After geographic conversion, a pure function separates markers whose centers are closer than 0.015 m. It pushes them apart equally along the line between them until the centers are at least 0.015 m apart, then clamps each marker to 0.02 m from its original city. Eight passes handle clusters. The 0.02 m city limit wins if both cannot be met.
- **Rationale**: The specification asks for a visible, tappable logo that stays next to its city. Doing this before anchors are created keeps the rule testable without ARCore.
- **Alternatives considered**: A stacked count, or hiding extra partners until a tap, was rejected in clarification. Leaving exact overlap fails the first-tap outcome.

## Local data

- **Decision**: Store the host, partners, agreements, logo files, and the reference map as app assets. Do not add a database or a download step. Parsing is specified in Catalog JSON below: `org.json` runs inside the Android `data` module. The `coordinates` module does not import Android.
- **Rationale**: The first version is a fixed sample set that must work with no network. Assets are the smallest store that satisfies that. City conversion stays testable without a phone.
- **Alternatives considered**: Room, a remote API, and user-imported files are outside the specification.

## Websites

- **Decision**: Open `https` addresses with a view intent only when a validated network is present. Otherwise leave the details on screen and say a connection is needed. Missing or non-https addresses are shown as unavailable and are not opened.
- **Rationale**: Exploration is offline. Opening a site is the one online step. Restricting the scheme avoids launching unexpected handlers.
- **Alternatives considered**: An in-app browser is extra scope. Always calling the intent fails the offline acceptance scenario.

## Devices that cannot run the experience

- **Decision**: Declare AR optional in the manifest (`minSdk` 24). On startup call `ArCoreApk.checkAvailability()`. `UNSUPPORTED_DEVICE_NOT_CAPABLE`, a declined Play Services for AR install, or a failed session shows one explanation and does not open the camera. Supported devices that lack Play Services for AR are asked to install it once. After it is installed, map tracking does not need a network.
- **Rationale**: The specification says an incapable phone still opens the app, explains the limit, and does not get a camera-only mode. AR Required would hide that path on the Play Store and is the wrong fit for this acceptance test. ARCore itself does not run below Android 7.0 (API 24).
- **Alternatives considered**: A camera preview without anchors was rejected. Guessing an allow-list of phone models is less accurate than ARCore's availability check. The exact `targetSdk` follows the current stable Android SDK at implementation time; it does not change this behavior.

## Camera permission

- **Decision**: Request the camera only after the device can run AR and Play Services for AR is present. Denial shows an explanation and a retry. Permanent denial uses the system app-settings screen as the retry path. The process is not restarted and the app is not reinstalled.
- **Rationale**: Matches the specification's deny, retry, and stay-open rules, and avoids asking for a camera the app cannot use.

## Tests

- **Decision**: JUnit on the JVM for coordinate conversion, overlap, catalog parsing, and the tracking-state to screen-state mapping. Device checks for recognition, anchoring, permission, and incapable phones live in the quickstart, not in automated camera tests.
- **Rationale**: The constitution requires automated tests for business rules and geographic conversion, and those do not need a camera. Camera tracking cannot be asserted reliably in CI.

## Reference image quality

- **Decision**: Before the image is bundled, score it with ARCore's `arcoreimg` tool and keep a score of at least 75. A distinctive border may be part of the same printed sheet. Geographic bounds then use the content rectangle inside that border.
- **Rationale**: Oceans and repeated labels are weak image targets. A border that is printed on the map sheet is still the predefined map, not a separate code.
- **Alternatives considered**: Adding a QR code was rejected. Tracking an unscored decorative poster is the main risk of missed recognition.

## Implementation language

- **Decision**: Application and test source is Java on JVM 17. Gradle build scripts may stay in the Kotlin DSL. This supersedes the earlier Kotlin application source. Product behavior does not change.
- **Rationale**: The requested change is the implementation language. The constitution requires Android, module boundaries, and JVM tests for rules and coordinates. It does not require Kotlin.
- **Alternatives considered**: Keeping a Kotlin UI or domain layer would leave application source in Kotlin.

## Screens

- **Decision**: Permission, failure, details, and the map host are Java views using Material Components. They do not reference ARCore or SceneView.
- **Rationale**: Jetpack Compose UI is written as Kotlin. Java views keep the same screens and wording without a Kotlin UI source set.
- **Alternatives considered**: Compose called from Java still needs Kotlin composable source. That does not meet the language change.

## Catalog JSON

- **Decision**: Parse `partners.json` and `world-map.json` with `org.json` inside the Android `data` module. Apply the same required fields, numeric ranges, and `https` rules as the catalog contract. This supersedes kotlinx.serialization. `coordinates` stays a plain Java module with no Android dependency.
- **Rationale**: kotlinx.serialization needs the Kotlin compiler plugin. `org.json` is available to the Android `data` library and its JVM unit tests, so no extra parser library is added. Geographic conversion does not need that parser.
- **Alternatives considered**: Gson or Moshi would also parse JSON from Java. They add a dependency the MVP does not need.
