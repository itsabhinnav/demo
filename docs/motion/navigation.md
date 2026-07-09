# Navigation Screen — Motion

Navigation focuses on content-swap emphasis and effects-driven panel visibility.

[← Back to master guide](../../motion.md) · [Dashboard hub](./dashboard.md)

---

## Turn instruction card

**Trigger:** Tap the card to cycle maneuvers.

**Motion:** Instruction text scales in from 0.9× and fades; outgoing text scales to 1.05× and fades out.

**Token:** `defaultSpatialSpec()`

**What to say:** *“Turn-by-turn updates emphasize the new instruction with a subtle scale pop — spatial spring on a content swap.”*

---

## Route steps panel

**Trigger:** Toggle **Steps** chip in the header.

**Motion:** Steps list fades in/out.

**Token:** `defaultEffectsSpec()`

---

## Emphasized detail card

**What:** The turn card uses `DetailSurfaceCard(emphasized = true)` which applies a slight scale-up (1.02×).

**Token:** `defaultSpatialSpec()`

---

## Source files

| Area | Files |
|------|-------|
| Screen | `app/.../navigation/NavigationScreen.kt` |
| Components | `app/.../navigation/components/NavigationComponents.kt`, `DummyMapBackground.kt` |
| Detail cards | `app/.../ivi/common/DetailSurfaceCard.kt` |
