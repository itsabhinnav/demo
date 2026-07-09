# Dashboard Hub — Motion

The widget grid is the entry point for all shared transitions and live card previews.

[← Back to master guide](../../motion.md)

---

## Container transform (widget open / close)

**What:** Tapping a widget card morphs it into its full-screen detail view. Back collapses it to the grid slot.

**Implementation:** `SharedTransitionLayout` + `sharedBounds` on the card container, with matching `sharedElement` keys for icon, title, content, and controls.

**Shared keys per widget:**

| Element | Key pattern |
|---------|-------------|
| Card shell | `widget_<name>` |
| Icon | `widget_<name>_icon` |
| Title | `widget_<name>_title` |
| Main content | `widget_<name>_content` |
| Controls row | `widget_<name>_controls` |

**What to say:** *“This is a Material container transform — the card bounds morph into the detail surface while the icon, title, and controls stay visually connected. No hard cut; the layout carries continuity.”*

**Tip:** Open Climate or Media first — they have the richest shared elements (dial, album art, transport bar).

---

## Predictive back

**What:** While a widget is expanded, a back gesture previews the collapse before committing.

**Implementation:** `PredictiveBackHandler` on `IviDemoScreen`; cancels cleanly if the gesture is aborted.

**What to say:** *“Predictive back lets the user peek at the return destination before completing navigation.”*

---

## Live widget shape morphing (grid only)

Cards react to widget state **before** you open them:

| Widget | Trigger | Shape change |
|--------|---------|--------------|
| Climate | A/C on | Rest corners → active asymmetric radii |
| Media | Playing | Rest corners → playing asymmetric radii |
| Vehicle | Sport mode or charging | Rest corners → active asymmetric radii |

**Token:** `defaultSpatialSpec()` via `rememberMorphingRoundedShape`.

**What to say:** *“Expressive shape isn’t static — corner radii morph with state so the card feels alive on the dashboard.”*

---

## Placeholder widgets

Energy, Calls, Camera, and Trips use the same **container transform** and shared elements (icon, title, content, controls) but with dummy list content. Motion behavior matches the patterns above.

---

## Source files

| Area | Files |
|------|-------|
| Shared transitions | `app/.../dashboard/WidgetSharedTransition.kt`, `IviDemoScreen.kt` |
| Widget cards | `app/.../dashboard/components/DashboardWidgetCard.kt`, `WidgetEmbeddedContent.kt` |
| Shape morphing | `app/.../theme/MorphingRoundedShape.kt` |
