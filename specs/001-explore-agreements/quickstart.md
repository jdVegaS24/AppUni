# Quickstart: Explore University Agreements on a World Map

Validation guide for the MVP. Implementation steps belong in `tasks.md`.

## Prerequisites

- Android Studio with SDK 24 or newer installed.
- A phone that supports ARCore, with Google Play Services for AR installed, for the device scenarios.
- A second phone that does not support ARCore, or an emulator without ARCore, for the unsupported-device scenario.
- The predefined world map printed at the width stored in `assets/maps/world-map.json`.
- The reference image scored at 75 or higher with ARCore's `arcoreimg` tool before it is bundled.
- Sample partners include two cities in one country and at least one pair close enough to overlap at the chosen logo size.

## Automated checks

From the repository root:

```text
./gradlew :coordinates:test :domain:test :data:test
```

Expected:

- Corner, edge, and center cities match the image-local meters described in [research.md](research.md).
- Cities outside the map frame produce no point.
- Overlapping cities separate until their centers are at least 1.5 cm apart, and every marker stays within 2 cm of its original city.
- Catalog JSON that breaks [contracts/partner-catalog.schema.json](contracts/partner-catalog.schema.json) fails parsing.
- A blank or non-`https` website is not openable.
- `TRACKING` becomes `MapTracked`. `PAUSED` and `STOPPED` become `MapLost` with no markers.

These tests do not open a camera.

## Device: happy path

1. Install the app on an ARCore phone.
2. Allow the camera when asked.
3. Point at the printed map until markers appear.
4. Confirm each visible partner logo sits on its city, including two cities in one country.
5. Move the phone around the sheet for about 10 seconds. Logos stay on those cities.
6. Tap a logo. Confirm name, city, country, logo, agreement type, description, and website.
7. Tap a second logo. The first partner's fields are gone.
8. Dismiss the details. The logos are still on the cities.

Expected timing: logos for cities in view appear within 3 seconds of the image becoming tracked.

## Device: overlap, offline, loss, permission

- Use the close city pair. Both logos are visible and each tap opens the matching partner.
- Enable airplane mode. Markers and details still open. Choosing the website shows that a connection is needed and does not leave the details.
- Turn the phone away from the map, then aim at it again. Logos return to the same cities.
- Cover the map or move so tracking stops. Logos disappear, the map-lost message is shown, and aiming at the map restores them.
- Deny the camera. The app stays open, explains the denial, and retry asks again. After a later grant, the camera starts without reinstalling.

## Device: cannot run AR

1. Install the same build on a phone that cannot keep markers attached to a printed image.
2. Start the app.

Expected: an explanation that this phone cannot run the map experience. The app stays open. The camera is not shown.
