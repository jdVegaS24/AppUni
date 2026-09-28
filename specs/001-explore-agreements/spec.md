# Feature Specification: Explore University Agreements on a World Map

**Feature Branch**: `001-explore-agreements`

**Created**: 2026-09-28

**Status**: Draft

**Input**: User description: "Build an Android augmented-reality application for exploring the international agreements of a host university. A user points the phone camera at a predefined physical world map. When the map is recognized, the application overlays markers on countries where the host university has partner universities. For example, when viewing Spain, the user should see markers for partner universities located in Spain. Each marker should display the university logo or crest. When the user taps a marker, show: university name, city, country, logo, type of agreement, description, website. The initial version should use mock university data. The user must be able to move the phone around the physical map while the virtual markers remain correctly anchored to the map."

## Clarifications

### Session 2026-09-28

- Q: What must the phone follow so the markers stay attached to the printed world map? → A: The phone recognizes the printed world map itself. No code and no manual alignment.
- Q: Where on that printed map should each partner university's marker sit? → A: At the partner's city on the printed map.
- Q: When two partner cities would put their markers on top of each other, what should the person see? → A: Nudge overlapping markers apart so each logo stays visible and tappable, still next to its city.
- Q: Must a person be able to explore the map and read partner details with no network connection? → A: Map, markers, and details work offline. Opening a website needs a connection, and failure is explained.
- Q: What should a person see on a phone that cannot keep markers attached to the printed map? → A: Explain that this phone cannot run the map experience, and stay open. No camera-only mode.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See partner universities on the map (Priority: P1)

A person stands at a predefined physical world map and points the phone camera at it. The phone recognizes that printed map itself. The person does not scan a code or line the overlay up by hand. When the map is recognized, a marker appears at each partner university's city on that printed map. Each marker shows that partner's logo or crest. Two partners in Spain appear at their own cities in Spain. A country with no partners shows none.

**Why this priority**: Seeing where agreements exist is the reason to point the phone at the map. Without markers, the experience does not help anyone explore the host university's international agreements.

**Independent Test**: Point an allowed camera at the predefined world map and confirm that every sample partner whose city is in view appears as a logo marker at that city, and that countries without partners stay unmarked.

**Acceptance Scenarios**:

1. **Given** the camera is allowed and the predefined printed world map is in view, **When** the phone recognizes that map itself, **Then** a marker appears at the city of every sample partner whose city is in view, without the person scanning a code or aligning the overlay by hand.
2. **Given** the sample set includes two partner universities in different cities in Spain, **When** those cities are in view and the map is recognized, **Then** each university has its own marker at its city, showing that university's logo or crest.
3. **Given** two sample partners whose cities would place their markers on top of each other, **When** the map is recognized, **Then** those markers are shifted until their centers are at least 1.5 cm apart, each marker stays within 2 cm of its city, and each logo stays tappable.
4. **Given** a country in view has no sample partners, **When** the map is recognized, **Then** that country shows no partner marker.
5. **Given** the first version, **When** markers are shown, **Then** the partners and agreements are the prepared sample set, and the person is not asked to enter or import them.
6. **Given** a sample partner's logo or crest cannot be displayed, **When** that partner's marker is shown, **Then** the marker shows a labeled stand-in with the university name.

---

### User Story 2 - Keep markers on the map while moving the phone (Priority: P1)

After markers appear, the person moves the phone around the physical map. The phone keeps following that same printed map. Each marker stays on the same city on that sheet instead of sliding around the screen or jumping to the wrong city. If the sheet is briefly outside the camera frame and the phone can still follow the map, the markers stay on those cities. Markers are hidden only when the phone can no longer follow the map.

**Why this priority**: A marker that leaves its city misleads the person about where the partner is. The exploration only works if the markers remain tied to those cities on the printed map while the phone moves.

**Independent Test**: With markers visible, move the phone around the map for several seconds while keeping the map in view, and confirm each marker remains on the same city.

**Acceptance Scenarios**:

1. **Given** partner markers are visible on the recognized map, **When** the person moves the phone around the map and the map stays in view, **Then** each marker remains on the same city it occupied before the movement.
2. **Given** markers are visible and the phone can still follow the map, **When** the sheet is briefly outside the camera frame, **Then** the markers stay on the same cities.
3. **Given** the phone can no longer follow the map, **When** the person aims at the map and it is followed again, **Then** the markers return to the same cities.

---

### User Story 3 - Read a partner's agreement (Priority: P2)

The person taps a marker to learn about that partner. The phone shows the university name, city, country, logo, type of agreement, description, and website. The person can leave the details and return to the map.

**Why this priority**: The markers answer "where." The details answer "who" and "what kind of agreement." That reading step is the rest of the exploration, and it depends on the markers already being in the right places.

**Independent Test**: Tap one visible marker and confirm all seven details match that partner, then use Close or Back and confirm the map markers are still there.

**Acceptance Scenarios**:

1. **Given** a partner marker is visible, **When** the person taps it, **Then** the details show that university's name, city, country, logo or crest, agreement type, description, and website.
2. **Given** the details for one partner are open, **When** the person taps a different marker, **Then** the details change to that other partner and do not keep the previous partner's information.
3. **Given** the details are open, **When** the person uses the Close control or the system Back action, **Then** the map view returns with the markers still on their cities.
4. **Given** a partner's website is shown and a network connection is available, **When** the person chooses to open it, **Then** that website is handed to the external viewer, and the same partner details are still available when the person returns. If the viewer cannot open the site, the viewer reports that failure.
5. **Given** a sample partner has no website, **When** the person opens that marker, **Then** the details say the website is unavailable and do not offer to open an address.
6. **Given** the phone has no network connection and a partner's details are open, **When** the person reads the details, **Then** the name, city, country, logo, agreement type, and description are still shown. **When** the person tries to open the website, **Then** they are told a connection is needed, and the map and details remain usable.

---

### User Story 4 - Recover when the camera or the map is unavailable (Priority: P2)

If the camera is not allowed, the predefined map cannot be recognized, or the phone cannot keep markers attached to the printed map, the person sees a clear explanation. The experience stays open. A phone that cannot keep markers attached does not get a camera-only view.

**Why this priority**: The happy path needs the camera and the printed map. People still need to understand what went wrong and how to continue, instead of facing a blank or frozen screen.

**Independent Test**: Deny the camera, aim away from the map, and open the experience on a phone that cannot keep markers attached. Confirm each case explains the problem and leaves the experience open, and that the last case does not offer a camera-only view.

**Acceptance Scenarios**:

1. **Given** the person has not allowed the camera, **When** they deny the request and the system will still ask, **Then** they see an explanation and can be asked again, and the experience stays open.
2. **Given** the system will not ask for the camera again, **When** the person needs the camera, **Then** they are told to enable camera permission in the application's system settings, and the experience stays open.
3. **Given** the camera is allowed but the predefined printed world map is not in view, or the phone cannot recognize that map itself, **When** recognition does not succeed, **Then** they are asked to aim at the predefined world map and can try again, and the experience stays open.
4. **Given** markers were visible, **When** the phone can no longer follow that printed map, **Then** markers are not left on the wrong cities, the person is told that the map was lost, and they can aim at the map again.
5. **Given** the person previously denied the camera and then allows it, **When** they try again, **Then** the camera view starts without reinstalling the experience.
6. **Given** the phone cannot keep markers attached to the printed map, **When** the person starts the experience, **Then** they see an explanation that this phone cannot run the map experience, the experience stays open, and no camera-only view is offered.

---

### Edge Cases

- A country has more than one partner. Each partner gets its own marker at its city, and each marker can be opened on its own.
- A country has no partners. No marker is shown there.
- Nearby cities would put markers on top of each other. Centers are separated until they are at least 1.5 cm apart, and no marker moves more than 2 cm from its city. If both limits cannot be met, the marker stays within 2 cm of its city. Tapping one opens that partner only.
- The printed map is briefly outside the camera frame while the phone can still follow it. Markers stay on their cities.
- The phone can no longer follow the printed map. Markers are hidden, the person is told the map was lost, and aiming at the map can restore the same cities.
- The person taps the camera view where there is no marker. Partner details do not open.
- A sample logo or crest cannot be displayed. The marker and the details show a labeled stand-in with the university name so the partner is still identifiable.
- A sample partner has no website. The details say the website is unavailable and do not offer to open a missing address.
- Only part of the map is in view. Markers appear only for partners whose cities are on the visible part of the recognized map.
- The person moves the phone quickly. Markers either stay on the correct cities or the experience reports that the map was lost, and they do not settle on the wrong city.
- The camera becomes unavailable after it was working. The person sees an explanation and a way to try again, and the experience stays open.
- The camera sees a different sheet, a photo, or a code instead of the predefined printed world map. That view is not treated as the map being recognized.
- The phone has no network connection. Markers and partner details still appear from the prepared sample set. Trying to open a website explains that a connection is needed.
- The phone cannot keep markers attached to the printed map. The person sees an explanation that this phone cannot run the map experience. The experience stays open and does not offer a camera view with no markers.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The experience MUST let a person point the phone camera at one predefined physical world map. The phone MUST recognize that printed map itself. The person MUST NOT need to scan a code or align the overlay by hand.
- **FR-002**: When that map is recognized, the experience MUST show a marker for each sample partner university whose city is in view.
- **FR-003**: Each marker MUST be placed at that partner university's city on the printed map, inside the country where the city is located. Partners in different cities in the same country, such as two universities in Spain, MUST appear at their own cities.
- **FR-004**: Each marker MUST display the partner university's logo or crest. If that image cannot be shown, the marker MUST display a labeled stand-in that names the university.
- **FR-005**: Countries with no sample partners MUST NOT show a partner marker.
- **FR-006**: The person MUST be able to move the phone around the physical map while the phone keeps following that printed map and each visible marker remains on the same city on that sheet. While the phone can still follow the map, markers MUST stay on those cities even if the sheet is briefly outside the camera frame.
- **FR-007**: If the phone can no longer follow the map and later follows it again, the experience MUST place the markers on the same cities as before.
- **FR-008**: If the phone can no longer follow that printed map, the experience MUST NOT leave markers sitting on the wrong cities. It MUST tell the person the map was lost and MUST let them aim at the map again.
- **FR-009**: Tapping a marker MUST show that partner's university name, city, country, logo or crest, agreement type, description, and website.
- **FR-010**: The details MUST describe only the partner whose marker was tapped. Opening another marker MUST replace the previous details.
- **FR-011**: The person MUST be able to leave the details with the Close control on the details or with the system Back action, and MUST return to the map with markers still placed on their cities.
- **FR-012**: When a website is available, the person MUST be able to open it from the details. When it is not available, the details MUST say so and MUST NOT offer a broken destination. When a network connection exists, opening the website MUST be handed to the external viewer. If that viewer cannot open the site, the viewer reports the failure and the experience MUST NOT add its own error flow. When the person returns from the viewer, the same partner details MUST still be available.
- **FR-013**: The first version MUST use a prepared sample set of host-university partners and agreements stored with the experience. The person MUST be able to see markers and read details with no network connection. The person MUST NOT need to create, edit, or load that information.
- **FR-014**: If camera use is denied or the camera cannot be used, the experience MUST explain the problem and MUST stay open. A first denial that the system will still ask MUST be requestable again. If the system will not ask again, the experience MUST explain that camera permission has to be enabled in the application's system settings.
- **FR-015**: If the camera is on and the predefined printed map itself is not yet recognized, including when the camera is aimed at a different sheet, the experience MUST tell the person to aim at that map, MUST keep the experience open, and MUST NOT show partner markers. The person MUST NOT be asked to scan a code or align the overlay by hand.
- **FR-016**: After the person allows a camera that was previously denied, trying again MUST start the camera view without reinstalling the experience.
- **FR-017**: Each logo or stand-in is 1.5 cm across. When marker centers would be closer than 1.5 cm, the experience MUST shift them equally until the centers are at least 1.5 cm apart, or until each marker has moved 2 cm from its city, whichever comes first. A marker MUST NOT move more than 2 cm from its city. The experience MUST NOT collapse those partners into one marker or hide a partner until another marker is tapped.
- **FR-018**: Opening a partner website MUST require a network connection. If that connection is unavailable, the experience MUST explain that a connection is needed and MUST leave the map and the open details usable.
- **FR-019**: If the phone cannot keep markers attached to the printed map, the experience MUST explain that this phone cannot run the map experience, MUST stay open, and MUST NOT offer a camera-only mode.

### Key Entities *(include if feature involves data)*

- **Host University**: The single institution whose international agreements are being explored. The first version has one host, fixed in the sample content.
- **Partner University**: A university that has an agreement with the host. It has one city on the printed map. Attributes shown to the person: name, city, country, logo or crest, and website. The marker is placed at that city.
- **Agreement**: The relationship between the host and one partner. Attributes shown to the person: type and description. The sample set includes one agreement per partner.
- **World Map**: The one predefined printed world map the person points the phone at. The phone recognizes that sheet itself. Partner cities have positions on this map.
- **Marker**: The on-screen symbol for one partner, shown at that partner's city, carrying the logo or crest, and opening that partner's details when tapped.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: After the phone recognizes the predefined printed world map itself, with no code and no manual alignment, a person sees a marker at the city of every sample partner whose city is in view within 3 seconds.
- **SC-002**: In a check of the sample set, 100% of partners appear as markers at their own cities, including two partners in different cities in the same country, and 100% of countries with no partners show no marker.
- **SC-003**: While a person moves the phone around the map for 10 seconds and keeps the map in view, every marker center stays within 2 cm, on the printed map, of the position it occupied at the start of that movement.
- **SC-004**: 100% of sample partners, when their marker is tapped, present the university name, city, country, logo or crest, agreement type, description, and website.
- **SC-005**: In a manual check of 10 first-time participants, at least 9 open the requested partner's details on the first tap without help. A correct first selection is that first tap opening the details of the one partner they were asked to select.
- **SC-006**: In every trial where the camera is denied, the camera cannot be used, or the predefined map cannot be recognized, the person sees an explanation and a way to try again, and the experience remains open.
- **SC-007**: When sample partners would overlap, 100% of those markers stay separately tappable, their centers are at least 1.5 cm apart unless a 2 cm city limit prevents it, and every marker stays within 2 cm of its city.
- **SC-008**: With no network connection, a person can still see markers and open details for 100% of the sample partners whose cities are in view. Trying to open a website shows an explanation in every such trial.
- **SC-009**: In every trial on a phone that cannot keep markers attached to the printed map, the person sees an explanation that this phone cannot run the map experience, the experience stays open, and no camera-only view appears.

## Assumptions

- The person is anyone at the physical map with the phone. No account or sign-in is required.
- The first version is used on an Android phone that can keep markers attached to the predefined printed world map. The phone recognizes that printed map itself. A phone that cannot keep markers attached is told so and is not given a camera-only mode. Desktop use, other maps, codes, and manual alignment are out of scope.
- There is one host university. The person does not choose or switch hosts. The host is identified in the sample content.
- The sample set includes partners in more than one country and at least two partners in different cities in the same country, so placement at the city can be checked. Each sample partner has a city that can be pointed to on the predefined printed map.
- Each sample partner has one agreement. Multiple agreements with the same partner are out of scope for the first version.
- Sample content and on-screen wording are in English.
- "Website" means the address is visible in the details and, when a network connection is present, can be opened on the phone. The prepared sample set, including logos, is stored with the experience. Map exploration does not depend on a network connection.
- The person can grant or deny camera use. The experience asks for the camera when it is needed to view the map.
- Out of scope for the first version: editing agreements, searching a directory away from the map, live updates from a university office, a live catalog that must be downloaded before the map can be used, directions to a campus, a camera-only mode, and any map other than the predefined physical world map.
