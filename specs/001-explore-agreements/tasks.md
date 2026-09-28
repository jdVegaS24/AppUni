# Tasks: Explore University Agreements on a World Map

**Input**: Design documents from `/specs/001-explore-agreements/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md, constitution

**Tests**: Included. The constitution requires automated tests for business rules and geographic-to-map conversion, and this task request asks for those tests. Write each listed test so it fails before the matching implementation.

**Organization**: Tasks are grouped by user story so each story can be implemented and checked on its own.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependency on an incomplete sibling task)
- **[Story]**: User story label. Setup, foundational, and polish tasks have no story label
- Every task includes a file path

## Path Conventions

Gradle modules at the repository root: `app/`, `domain/`, `coordinates/`, `data/`, `ar/`. Packages use `appuni.explore`.

T001–T049 are the completed Kotlin implementation record. Do not uncheck them. Application and test source for the migration lives under `src/main/java` and `src/test/java`. T050 onward replaces that Kotlin source with Java and does not change product behavior.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Android project skeleton and AR configuration

- [X] T001 Create `settings.gradle.kts` with modules `app`, `domain`, `coordinates`, `data`, and `ar`
- [X] T002 Configure Kotlin JVM 17, Android minSdk 24, Jetpack Compose, kotlinx-serialization, and JUnit in `gradle/libs.versions.toml`, and add SceneView `arsceneview` only in `ar/build.gradle.kts`
- [X] T003 [P] Declare AR optional (`com.google.ar.core` optional) and the `CAMERA` permission in `app/src/main/AndroidManifest.xml`, with no camera-only entry that starts without AR
- [X] T004 [P] Create asset directories `app/src/main/assets/catalog/logos/` and `app/src/main/assets/maps/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Catalog rules, map geometry, coordinate conversion, and overlap math that every story uses

**Critical**: No user story work starts until this phase is complete

### Tests

- [X] T005 [P] Write failing JVM tests that reject a blank host id or name, duplicate partner ids, latitude outside -90 through 90, longitude outside -180 through 180, a blank agreement type or description, and a non-https website as not openable in `domain/src/test/kotlin/appuni/explore/domain/CatalogRulesTest.kt`
- [X] T006 [P] Write failing JVM tests for equirectangular conversion of the west, east, north, and south edges, the image center, a southern city to positive Z, a point outside the frame omitted, and an inset content rectangle in `coordinates/src/test/kotlin/appuni/explore/coordinates/GeoToImageTest.kt`
- [X] T007 [P] Write failing JVM tests that centers closer than 0.015 m move apart equally until they are at least 0.015 m apart, no marker moves more than 0.02 m from its city, the 0.02 m limit wins when both cannot be met, and partners are not collapsed into one marker in `domain/src/test/kotlin/appuni/explore/domain/MarkerSpreadTest.kt`
- [X] T008 [P] Write failing JVM tests that `TRACKING` maps to `MapTracked` and `PAUSED` or `STOPPED` maps to `MapLost` with no markers in `domain/src/test/kotlin/appuni/explore/domain/TrackingStatusTest.kt`
- [X] T009 [P] Write failing parser tests for a fixture that matches `specs/001-explore-agreements/contracts/partner-catalog.schema.json` and a broken fixture in `data/src/test/kotlin/appuni/explore/data/CatalogParserTest.kt`

### Models and rules

- [X] T010 [P] Add `HostUniversity` with required id unique in the catalog and required non-blank name in `domain/src/main/kotlin/appuni/explore/domain/HostUniversity.kt`
- [X] T011 [P] Add `Agreement` with required non-blank type and required non-blank description, one agreement per partner, in `domain/src/main/kotlin/appuni/explore/domain/Agreement.kt`
- [X] T012 [P] Add `PartnerUniversity` with required non-blank id, name, city, and country, latitude -90 through 90, longitude -180 through 180, optional logoFile, and optional website in `domain/src/main/kotlin/appuni/explore/domain/PartnerUniversity.kt`
- [X] T013 [P] Add `MapFrame` with required id and imageFile, physicalWidthMeters greater than 0, imagePixelWidth and imagePixelHeight greater than 0, projection, north, east greater than west, northLat greater than south, and a content UV rectangle with right greater than left and bottom greater than top in `domain/src/main/kotlin/appuni/explore/domain/MapFrame.kt`
- [X] T014 [P] Add states CheckingDevice, DeviceUnsupported, NeedsCameraPermission, SearchingForMap, MapTracked, MapLost, and DetailsOpen in `domain/src/main/kotlin/appuni/explore/domain/ExperienceState.kt`
- [X] T015 [P] Implement blank, null, and non-https website rejection in `domain/src/main/kotlin/appuni/explore/domain/CatalogRules.kt`
- [X] T016 [P] Implement equirectangular `GeoToImage` (+X to the right, +Z toward the bottom, origin at the image center, omit positions outside the frame) in `coordinates/src/main/kotlin/appuni/explore/coordinates/GeoToImage.kt`
- [X] T017 [P] Implement `MarkerSpread` with equal separation to 0.015 m between centers, a clamp of 0.02 m from the original city, the 0.02 m limit winning when both cannot be met, and eight passes in `domain/src/main/kotlin/appuni/explore/domain/MarkerSpread.kt`
- [X] T018 [P] Implement `TrackingStatusMapper` in `domain/src/main/kotlin/appuni/explore/domain/TrackingStatusMapper.kt`
- [X] T019 Implement `CatalogParser` for the partner catalog schema in `data/src/main/kotlin/appuni/explore/data/CatalogParser.kt`
- [X] T020 Implement `AssetCatalogSource` for `app/src/main/assets/catalog/partners.json`, logo files, and `app/src/main/assets/maps/world-map.json` in `data/src/main/kotlin/appuni/explore/data/AssetCatalogSource.kt`

**Checkpoint**: JVM tests for catalog rules, conversion, overlap, and tracking status pass. User stories can start.

---

## Phase 3: User Story 1 - See partner universities on the map (Priority: P1)

**Goal**: On an ARCore phone with the camera already allowed, recognizing the printed map shows a logo (or a named stand-in) at each in-view partner city from the bundled sample set. Nearby logos are nudged apart. Countries with no partners stay empty.

**Independent Test**: Point an allowed camera at the printed map and confirm every in-view sample partner appears at its city, two cities in one country are distinct, an overlapping pair stays separately visible, and a country with no partners has no marker.

- [X] T021 [P] [US1] Write `app/src/main/assets/maps/world-map.json` for one north-up equirectangular frame with physicalWidthMeters greater than 0, pixel dimensions greater than 0, east greater than west, northLat greater than south, and a content UV rectangle
- [X] T022 [P] [US1] Write `app/src/main/assets/catalog/partners.json` with one host, partners in more than one country, two partners in different cities of the same country, and one pair whose city positions are closer than 1.5 cm on the printed map
- [X] T023 [P] [US1] Add partner logo files named by `logoFile` under `app/src/main/assets/catalog/logos/`, leaving a file out only when that partner must use the named stand-in
- [ ] T024 [US1] Score the candidate bitmap with `arcoreimg` and commit it as `app/src/main/assets/maps/world-map.png` only when the score is at least 75
- [X] T025 [US1] Build an augmented-image database from `world-map.png` and physicalWidthMeters, with plane finding disabled, in `ar/src/main/kotlin/appuni/explore/ar/MapImageDatabase.kt`
- [X] T026 [US1] Place one anchor per in-view partner from `GeoToImage` then `MarkerSpread` while the image `TrackingState` is `TRACKING` in `ar/src/main/kotlin/appuni/explore/ar/MarkerAnchorPlanner.kt`
- [X] T027 [US1] Draw each logo, or a stand-in labeled with the university name, in `ar/src/main/kotlin/appuni/explore/ar/MapArView.kt`
- [X] T028 [US1] Show `MapScreen` from `app/src/main/kotlin/appuni/explore/ui/MainActivity.kt` only when `CAMERA` is already granted, hosting `app/src/main/kotlin/appuni/explore/ui/MapScreen.kt` with no ARCore calls in that screen
- [X] T029 [US1] While the camera is on and the reference image is not `TRACKING`, show no partner markers and tell the person to aim at the predefined printed map in `app/src/main/kotlin/appuni/explore/ui/MapScreen.kt`

**Checkpoint**: User Story 1 works on a capable phone whose camera permission is already granted. Before the map is recognized, the screen tells the person to aim at it.

---

## Phase 4: User Story 2 - Keep markers on the map while moving the phone (Priority: P1)

**Goal**: Logos stay on the same cities while the phone moves and the fixed map is still followed, including `LAST_KNOWN_POSE` during `TRACKING`. They disappear instead of sitting on the wrong city when tracking stops, and they return to the same cities when tracking resumes.

**Independent Test**: With markers visible, move the phone for about 10 seconds while the map stays tracked and confirm each logo remains on its city. Aim away until tracking stops, then aim back and confirm the same cities return. The JVM test does not need a camera.

- [X] T030 [US2] Write a failing JVM test that `TRACKING` plus `LAST_KNOWN_POSE` stays `MapTracked`, and leaving `TRACKING` clears markers, in `domain/src/test/kotlin/appuni/explore/domain/FixedMapTrackingTest.kt`
- [X] T031 [US2] Treat `LAST_KNOWN_POSE` during `TRACKING` as still tracked in `domain/src/main/kotlin/appuni/explore/domain/TrackingStatusMapper.kt`
- [X] T032 [US2] Keep image-local anchors while `TRACKING` and remove them when the image is `PAUSED` or `STOPPED` in `ar/src/main/kotlin/appuni/explore/ar/MarkerAnchorPlanner.kt`
- [X] T033 [US2] Recreate the same city anchors when the image returns to `TRACKING` in `ar/src/main/kotlin/appuni/explore/ar/MapSessionController.kt`

**Checkpoint**: Movement and re-recognition keep cities stable. User Story 1 still shows the initial markers.

---

## Phase 5: User Story 3 - Read a partner's agreement (Priority: P2)

**Goal**: Tapping a logo shows that partner's name, city, country, logo or stand-in, agreement type, description, and website. Another tap replaces those details. Dismiss returns to the map. An `https` website opens only with a validated network. Otherwise the details stay and explain the block.

**Independent Test**: Tap one marker and match all seven fields, tap a second marker and confirm the first partner is gone, dismiss and confirm the logos remain, then try the website with and without a validated network.

- [X] T034 [US3] Write failing JVM tests that details describe only the selected partner, a second id replaces them, a blank or non-https website is unavailable, and no validated network yields `ConnectionRequired` rather than `OpenWebsite` in `domain/src/test/kotlin/appuni/explore/domain/DetailsPolicyTest.kt`
- [X] T035 [P] [US3] Implement selection, replacement, and website decisions in `domain/src/main/kotlin/appuni/explore/domain/DetailsPolicy.kt`
- [X] T036 [P] [US3] Report the tapped partner id from `ar/src/main/kotlin/appuni/explore/ar/MapArView.kt` without drawing the details inside the AR view
- [X] T037 [US3] Show name, city, country, logo or stand-in, agreement type, description, and website in `app/src/main/kotlin/appuni/explore/ui/PartnerDetailsSheet.kt`
- [X] T038 [US3] Open an `https` address with a view intent only when a validated network exists, and otherwise report that a connection is needed, in `app/src/main/kotlin/appuni/explore/ui/WebsiteOpener.kt`
- [X] T039 [US3] Replace open details when another marker is selected, and return to the map when the person uses Close on the details or the system Back action, in `app/src/main/kotlin/appuni/explore/ui/MapScreen.kt`

**Checkpoint**: Details and website behavior work on top of the tracked map.

---

## Phase 6: User Story 4 - Recover when the camera or the map is unavailable (Priority: P2)

**Goal**: An incapable phone, a declined AR install, or a failed session explains that this phone cannot run the map experience and does not open the camera. Camera denial explains the problem, offers retry, and a later grant starts the camera without reinstalling. Tracking loss hides markers and asks the person to aim at the map again.

**Independent Test**: Deny the camera and confirm explanation plus retry. On a phone that cannot attach markers to the printed map, confirm the explanation, that the app stays open, and that no camera view appears. After markers were visible, force tracking loss and confirm the logos are gone and aiming again restores them.

- [X] T040 [US4] Write failing JVM tests that an incapable device, a declined AR install, or a failed session maps to `DeviceUnsupported` with the camera closed, and camera denial maps to `NeedsCameraPermission`, in `domain/src/test/kotlin/appuni/explore/domain/DeviceAccessPolicyTest.kt`
- [X] T041 [US4] Implement those access decisions in `domain/src/main/kotlin/appuni/explore/domain/DeviceAccessPolicy.kt`
- [X] T042 [P] [US4] Show the incapable-phone explanation and do not start a camera preview in `app/src/main/kotlin/appuni/explore/ui/DeviceGateScreen.kt`
- [X] T043 [P] [US4] Call `ArCoreApk.checkAvailability()` before creating a session, and treat a declined Play Services for AR install as unsupported, in `ar/src/main/kotlin/appuni/explore/ar/ArAvailability.kt`
- [X] T044 [P] [US4] Request `CAMERA` only after AR is available. A first denial may be requested again. If the system will not ask again, explain that camera permission must be enabled in the application's system settings, in `app/src/main/kotlin/appuni/explore/ui/CameraPermissionScreen.kt`
- [X] T045 [US4] Show the map-lost explanation when the image is `PAUSED` or `STOPPED` or camera tracking fails in `app/src/main/kotlin/appuni/explore/ui/MapLostMessage.kt`
- [X] T046 [US4] Hide markers on that lost state and allow aiming at the map again from `ar/src/main/kotlin/appuni/explore/ar/MapSessionController.kt`

**Checkpoint**: Permission, unsupported-device, and tracking-loss paths match the specification. No camera-only mode exists.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Checks that apply to the finished MVP, without new product behavior

- [X] T047 [P] Remove any credential or non-https website from `app/src/main/assets/catalog/partners.json`
- [X] T048 Run `./gradlew :coordinates:test :domain:test :data:test` as specified in `specs/001-explore-agreements/quickstart.md`
- [ ] T049 Walk the device scenarios in `specs/001-explore-agreements/quickstart.md` and fix only mismatches with the existing specification

---

## Phase 8: Java migration

**Purpose**: Replace Kotlin application and test source with Java. Keep Android, ARCore, SceneView inside `ar`, module boundaries, assets, and every current behavior.

**Independent Test**: `./gradlew :coordinates:test :domain:test :data:test` passes, and `:app:assembleDebug` produces the debug APK. No `.kt` file remains under a module `src` tree.

- [X] T050 Enable Java 17 compilation beside the existing Kotlin sources in `gradle/libs.versions.toml`, `app/build.gradle.kts`, `domain/build.gradle.kts`, `coordinates/build.gradle.kts`, `data/build.gradle.kts`, and `ar/build.gradle.kts`. Keep SceneView only in `ar/build.gradle.kts`. Do not remove the Kotlin plugin until T057.
- [X] T051 Replace the domain types and rules with Java in `domain/src/main/java/appuni/explore/domain/` and delete the matching files under `domain/src/main/kotlin/`.
- [X] T052 Replace `GeoToImage` with Java in `coordinates/src/main/java/appuni/explore/coordinates/GeoToImage.java` and delete `coordinates/src/main/kotlin/appuni/explore/coordinates/GeoToImage.kt`.
- [X] T053 Replace catalog and map-frame loading with Java in `data/src/main/java/appuni/explore/data/CatalogParser.java` and `data/src/main/java/appuni/explore/data/AssetCatalogSource.java`, parse JSON with `org.json`, and delete the matching Kotlin files.
- [X] T054 Replace the eight JVM test classes with Java under `domain/src/test/java/`, `coordinates/src/test/java/`, and `data/src/test/java/`, covering the same cases as T005–T009, T030, T034, and T040, and delete the `.kt` tests.
- [X] T055 Replace the AR session, image database, anchors, and availability types with Java in `ar/src/main/java/appuni/explore/ar/`. Host SceneView's `ARSceneView` from `MapArView.java` with no Compose and no ARCore types exposed to `app`. Delete the matching Kotlin files.
- [X] T056 Replace permission, failure, details, and map host screens with Java views and Material Components in `app/src/main/java/appuni/explore/ui/` and `app/src/main/res/layout/`. Include `app/src/main/java/appuni/explore/ui/WebsiteOpener.java`: open an `https` address with a view intent only when a validated network exists, otherwise leave the details on screen and say a connection is needed, and do not open a blank or non-`https` address. Keep the same wording and flows. Delete the matching Kotlin files, including `WebsiteOpener.kt`.
- [X] T057 Remove the Kotlin, Compose, and kotlinx-serialization plugins and dependencies from `gradle/libs.versions.toml` and the module `build.gradle.kts` files, and delete any remaining `.kt` files under module `src` directories.
- [X] T058 Run `./gradlew :coordinates:test :domain:test :data:test` as specified in `specs/001-explore-agreements/quickstart.md`
- [X] T059 Run `./gradlew :app:assembleDebug` and confirm `app/build/outputs/apk/debug/app-debug.apk` is produced

**Checkpoint**: The debug APK is a Java Android app. Behavior matches the specification. T024 and T049 stay open.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Start immediately. T003 and T004 can run together after T001 and T002.
- **Foundational (Phase 2)**: Depends on Setup. Blocks every user story. Write T005–T009 first and see them fail, then T010–T014, then T015–T018, then T019 and T020.
- **User stories (Phases 3–6)**: Depend on Foundational. Story order for one developer is US1, US2, US3, US4.
- **Polish (Phase 7)**: Depends on the stories you intend to ship. T049 stays a device walk and is not part of the language migration.
- **Java migration (Phase 8)**: Starts after Phase 7 except T049. T050 first. Then T051, T052, and T053. T054 after those three. T055 after T051–T053. T056 after T055. T057 after T054–T056. T058 after T057. T059 after T058.

### User Story Dependencies

- **User Story 1 (P1)**: Starts after Foundational. No dependency on US2–US4. A device check needs a capable phone and a camera permission that is already granted. US4 adds denial and unsupported-device screens later.
- **User Story 2 (P1)**: JVM test T030 is independent after Foundational. The device check needs US1 anchors. It must not change city placement from US1.
- **User Story 3 (P2)**: Needs a tracked marker from US1 to tap. `DetailsPolicy` and `WebsiteOpener` do not require US2.
- **User Story 4 (P2)**: Device-gate and permission screens wrap US1. Tracking-loss copy uses the US2 session controller. The access-policy test does not need a camera.

### Within Each User Story

- Tests fail before the implementation in that story
- Sample assets before the image database
- Image database before anchors
- Anchors before the screen that hosts them. "Compose host" applies only to the historical Kotlin tasks in Phases 3–6. Phase 8 hosts `ARSceneView` from Java in T055 and does not use Compose.
- Policy types before the screens that render them

### Parallel Opportunities

- T003 and T004
- T005 through T009
- T010 through T014
- T015 through T018
- T021, T022, and T023
- T035 and T036 after T034 (different files; T037 and T038 wait until T035 is done, then they can proceed together because they do not share a file)
- T042, T043, and T044 after T041
- T051, T052, and T053 after T050 (different modules)
- T054 after T051–T053

---

## Parallel Example: User Story 1

```text
T021 world-map.json
T022 partners.json
T023 logo files
```

Those three assets do not share files. T024 waits until the bitmap exists, and T025 waits on T024.

## Parallel Example: User Story 3

```text
T035 DetailsPolicy.kt
T036 marker tap in MapArView.kt
```

After T035 is done, these two do not share a file:

```text
T037 PartnerDetailsSheet.kt
T038 WebsiteOpener.kt
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Finish Phase 1 and Phase 2.
2. Finish Phase 3.
3. Stop. On a capable phone with the camera already allowed, confirm the aim-at-the-map prompt before recognition, then cities, logos, the 1.5 cm overlap rule, and empty countries.
4. Do not add details, website opening, or failure screens until that check passes.

### Incremental Delivery

1. Setup and foundation.
2. User Story 1: see partners on the map.
3. User Story 2: markers stay on those cities while the phone moves.
4. User Story 3: details and websites.
5. User Story 4: permission, unsupported phones, and tracking loss.
6. Polish runs the quickstart checks.
7. Phase 8 replaces Kotlin application and test source with Java, then reruns the JVM tests and the debug APK build.

### Parallel Team Strategy

After Phase 2, one person can own US1 and US2 (they share the anchor planner), while another writes the US3 policy tests and `DetailsPolicy` against fixture partner ids. US4's `DeviceAccessPolicy` test can also start immediately. Do not edit `MarkerAnchorPlanner.kt` or `MapSessionController.kt` from two stories at the same time.

## Notes

- [P] tasks use different files and do not wait on each other
- Story labels trace US1–US4 only
- Do not add search, editing, a live catalog, directions, other maps, codes, manual alignment, or a camera-only mode
- Phase 8 does not change observable behavior and does not mark T024 or T049 complete
- Commit after each task or a small group that leaves the tests you just added passing
