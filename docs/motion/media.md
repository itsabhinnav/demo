# Media Screen — Motion

Media highlights shared album art, icon morphing, track transitions, and a sliding queue panel.

[← Back to master guide](../../motion.md) · [Dashboard hub](./dashboard.md)

---

## Album art shared element

**Trigger:** Open Media from grid.

**Motion:** Album art morphs from compact thumbnail to large hero — same shared key (`album_art`) in grid and detail.

**What to say:** *“Album art is a shared element — it scales and repositions without disappearing and reappearing.”*

---

## Album / card shape morph

**Trigger:** Play or pause.

**Motion:** Album and widget card corners shift between rest and playing radii.

**Token:** `defaultSpatialSpec()`

---

## Track info transition

**Trigger:** Next / previous track.

**Motion:** Title, artist, (album) slide vertically with fade — incoming from below, outgoing upward.

**Tokens:** `defaultSpatialSpec()` (slide) + `defaultEffectsSpec()` (fade)

**What to say:** *“Track changes combine spatial slide with effects fade — spatial for direction, effects for opacity.”*

---

## Play / pause morph

**Trigger:** Tap transport play/pause.

**Motion:**
- Button shell scales slightly when paused (0.96×)
- Icon cross-fades with scale in/out between Play and Pause glyphs

**Token:** `defaultSpatialSpec()`

---

## Queue panel width

**Trigger:** Tap queue icon in transport bar.

**Motion:** Side panel animates between `0.dp` and `400.dp`.

**Token:** `slowSpatialSpec()`

**What to say:** *“Secondary panels slide open with slow spatial so the layout reflow feels intentional, not abrupt.”*

---

## Source files

| Area | Files |
|------|-------|
| Screen | `app/.../media/MediaPlayerScreen.kt` |
| Components | `app/.../media/components/MorphingPlayPauseButton.kt`, `MediaDetailComponents.kt`, `MediaAlbumArt.kt`, `MediaTransportControlsBar.kt` |
| Shapes | `app/.../theme/MorphingRoundedShape.kt` |
