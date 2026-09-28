# Experience Contract

The `ar` module reports these states. The `app` module renders them. UI code does not read ARCore types.

## Device and camera

| Input | Output |
|-------|--------|
| Availability still checking | `CheckingDevice` |
| Device incapable, install declined, or session failed | `DeviceUnsupported` with the map-experience explanation. The camera is not started. |
| AR available, camera not granted | `NeedsCameraPermission` |
| Camera denied, and the system will still ask | Stay on `NeedsCameraPermission`. The request may be made again. |
| Camera denied, and the system will not ask again | Stay on `NeedsCameraPermission`. Explain that camera permission must be enabled in the application's system settings. |
| Camera granted and image not tracking | `SearchingForMap`. Tell the person to aim at the predefined printed map. Show no partner markers. |

## Map

| Input | Output |
|-------|--------|
| Reference image `TRACKING` | `MapTracked` plus one entry per `PlacedMarker` whose city is in the tracked image |
| Image leaves `TRACKING` after it had been tracking, or camera tracking fails | `MapLost`. Marker entries are empty. |
| Image returns to `TRACKING` | `MapTracked` with the same partner ids at the same image-local points |

`LAST_KNOWN_POSE` while `TRACKING` still counts as `MapTracked`. The map is fixed. That method means the sheet is briefly out of the camera while the pose is still held.

## Markers and details

| Input | Output |
|-------|--------|
| Tap a marker id | `DetailsOpen` for that partner: name, city, country, logo or stand-in, agreement type, description, website or "unavailable" |
| Tap a different marker | Replace the open partner. Do not keep the previous fields. |
| Tap empty space | No change |
| Close control or system Back | Return to `MapTracked` |
| Open website, address missing or not `https` | Stay on details. Do not emit an external open. |
| Open website, no validated network | Stay on details. Emit `ConnectionRequired`. |
| Open website, validated network and `https` | Emit `OpenWebsite` with that URL. The external viewer reports a failure to open the site. The same partner details are still available when the person returns. |

## Catalog file

`contracts/partner-catalog.schema.json` is the asset contract for `assets/catalog/partners.json`. The map frame is a separate asset, `assets/maps/world-map.json`, whose fields are the `MapFrame` in `data-model.md`. Logo files and `assets/maps/world-map.png` are bundled next to those files. No runtime download is part of this contract.
