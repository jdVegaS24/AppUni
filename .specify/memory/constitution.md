<!--
Sync Impact Report
- Version change: unratified template → 1.0.0
- Modified principles:
  - [PRINCIPLE_1_NAME] → I. Android World-Map AR
  - [PRINCIPLE_2_NAME] → II. Layered Modules
  - [PRINCIPLE_3_NAME] → III. Automated Tests (NON-NEGOTIABLE)
  - [PRINCIPLE_4_NAME] → IV. Graceful Camera and Tracking Failure
  - [PRINCIPLE_5_NAME] → V. MVP Simplicity
- Added sections:
  - Security Constraints
  - Quality Gates
- Removed sections: none (template placeholders replaced)
- Follow-up TODOs: none
-->

# AppUni Constitution

## Core Principles

### I. Android World-Map AR

AppUni MUST run on Android. The application MUST recognize a predefined
physical world map and overlay information about international university
agreements.

Rationale: The first product is a device experience tied to one known
physical map, so specs and implementation stay on that recognition-and-overlay
path.

### II. Layered Modules

The codebase MUST separate UI, domain, data, and augmented-reality logic
into distinct modules. AR-specific logic MUST NOT be embedded in UI
components. Geographic-to-AR coordinate conversion MUST be an independent
component that can be tested without a camera or a UI.

Rationale: Tracking, screens, agreement data, and placement math change for
different reasons and must be replaceable on their own.

### III. Automated Tests (NON-NEGOTIABLE)

Business logic and geographic-to-AR coordinate conversion MUST have automated
tests. A change to either area is incomplete until tests cover the changed
behavior.

Rationale: Agreement rules and coordinate math can be verified without a
device. Those checks are the gate for merging changes in those areas.

### IV. Graceful Camera and Tracking Failure

Denied or unavailable camera permission and AR tracking failure MUST NOT
crash the application. The user MUST see a clear failure state and a way to
recover or continue within the limits of that failure.

Rationale: Camera access and tracking are outside the app's control. The
session has to stay usable when either one fails.

### V. MVP Simplicity

The first version MUST remain an MVP. Implementations MUST avoid unnecessary
complexity and MUST prefer maintainability and clarity over premature
optimization. Capability beyond visualizing agreements on the recognized
world map requires an explicit specification change or a later version.

Rationale: Extra scope in the first release makes the AR path harder to
keep correct and harder to change.

## Security Constraints

Secrets, API keys, and credentials MUST NOT be stored in source code or in
committed configuration that ships with the repository. Values that vary by
environment MUST be supplied from outside the source tree.

## Quality Gates

Feature work MUST follow the Spec Kit flow: specification, plan, and tasks
before implementation. Plans MUST state how module boundaries are preserved
and where business rules and coordinate conversion are tested. Augmented-reality
behavior that depends on a device MUST still keep that pure logic covered by
automated tests that run off-device.

## Governance

This constitution supersedes conflicting practice for AppUni. An amendment
MUST update this file, MUST bump the semantic version, and MUST include a
Sync Impact Report comment until that amendment is committed. Version bumps
follow these rules:

- MAJOR: a principle is removed or redefined in a way that breaks existing
  governance.
- MINOR: a principle or section is added, or guidance is materially expanded.
- PATCH: wording is clarified without changing the rule.

Reviews of specifications, plans, and implementation MUST check compliance
with these principles. Complexity beyond the MVP MUST be justified in the
feature plan.

**Version**: 1.0.0 | **Ratified**: 2026-09-28 | **Last Amended**: 2026-09-28
