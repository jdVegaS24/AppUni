# Data Model: Explore University Agreements on a World Map

Sample content is read-only. Nothing in this model is edited at runtime.

## HostUniversity

The single institution whose agreements are explored.

| Field | Rules |
|-------|--------|
| id | Required. Unique in the catalog. |
| name | Required. Non-blank. Shown only as catalog identity. The first version does not ask the person to pick a host. |

Relationship: one host has many partners.

## PartnerUniversity

One university placed at one city on the printed map.

| Field | Rules |
|-------|--------|
| id | Required. Unique among partners. |
| name | Required. Non-blank. |
| city | Required. Non-blank display name. |
| country | Required. Non-blank display name. |
| latitude | Required. Decimal degrees, -90 through 90. Must fall inside the map frame or the partner is not shown. |
| longitude | Required. Decimal degrees, -180 through 180. Must fall inside the map frame or the partner is not shown. |
| logoFile | Optional path inside the catalog assets. Missing or unreadable files use a labeled stand-in. |
| website | Optional absolute `https` URL. Null, blank, or any other scheme is unavailable and must not be opened. |

Relationship: exactly one agreement.

## Agreement

| Field | Rules |
|-------|--------|
| type | Required. Non-blank. One type per partner in this version. |
| description | Required. Non-blank. |

## MapFrame

Describes the one printed reference image. Not shown as a marker.

| Field | Rules |
|-------|--------|
| id | Required. The augmented-image name. |
| imageFile | Required asset path of the reference bitmap. |
| physicalWidthMeters | Required. Greater than 0. The width passed to the image database. |
| imagePixelWidth, imagePixelHeight | Required. Both greater than 0. Height in meters is `physicalWidthMeters * imagePixelHeight / imagePixelWidth`. |
| projection | Required. MVP value is `equirectangular`. |
| north | Required. MVP value is `top`. |
| west, east | Required longitudes. `east` must be greater than `west`. |
| south, northLat | Required latitudes. `northLat` must be greater than `south`. |
| contentLeft, contentTop, contentRight, contentBottom | UV rectangle of the geographic area inside the tracked image. Each value is from 0 to 1. Right greater than left. Bottom greater than top. Use the full image when there is no border. |

## ImageLocalPoint

Output of coordinate conversion. Pure value. Not stored in the catalog.

| Field | Meaning |
|-------|---------|
| partnerId | Partner this point belongs to. |
| xMeters | Right of image center, in the image plane. |
| zMeters | Toward the bottom of the image, in the image plane. |
| usesStandIn | True when no logo file can be shown. |

## PlacedMarker

`ImageLocalPoint` after overlap separation.

| Field | Meaning |
|-------|---------|
| partnerId | Unchanged. |
| xMeters, zMeters | City point plus a nudge. Distance from the original city must be at most 0.02 m. |
| usesStandIn | Copied from the local point. |

## Screen states

These are derived. They are not stored.

| State | When |
|-------|------|
| CheckingDevice | ARCore availability has not resolved. |
| DeviceUnsupported | The phone cannot keep markers on the map, install was declined, or the session failed. No camera view. |
| NeedsCameraPermission | AR is available and the camera is not granted. |
| SearchingForMap | Camera is on and the reference image is not `TRACKING`. |
| MapTracked | Image is `TRACKING`. Markers use `PlacedMarker` positions. |
| MapLost | Image was tracking and is now `PAUSED` or `STOPPED`, or camera tracking failed. Markers are hidden. |
| DetailsOpen | A marker was tapped while `MapTracked`. Shows that partner only. |

Opening another marker replaces `DetailsOpen`. Dismissing it returns to `MapTracked`.

## Validation owned by tests

- One host, one agreement per partner, unique partner ids.
- Latitude and longitude ranges, and rejection outside the map frame.
- Corner and center cities land on the expected image-local meters.
- Overlapping cities separate until their centers are at least 0.015 m apart, or until each marker is 0.02 m from its original city.
- A missing website does not produce an openable address.
- `PAUSED` and `STOPPED` map to `MapLost` with no visible markers. `TRACKING` maps to `MapTracked`.
