# Material 3 Expressive Features — Presentation Guide

A feature-first overview of every **Material 3 Expressive** capability used in this demo. Use this when you want to present by *concept* (container transform, shape morphing, etc.) rather than by screen.

[← Back to master guide](../../motion.md)

---

## One-liner opener

> *“This sample demonstrates Material 3 Expressive end-to-end: expressive motion tokens, container transforms, morphing shapes, shared elements, directional content transitions, dynamic color, and layout reflow — all tuned for automotive, with motion clamped to Standard while driving.”*

---

## Feature summary

| # | Feature | Where to show | Screen doc |
|---|---------|---------------|------------|
| 1 | [Motion scheme & tokens](#1-motion-scheme--tokens) | Home side panel | [home.md](./home.md) |
| 2 | [Container transform](#2-container-transform) | Dashboard → tap widget | [dashboard.md](./dashboard.md) |
| 3 | [Shape morphing](#3-shape-morphing) | Dashboard grid, detail screens | [dashboard.md](./dashboard.md), [climate.md](./climate.md), [media.md](./media.md), [vehicle.md](./vehicle.md) |
| 4 | [Expressive shape tokens](#4-expressive-shape-tokens) | Cards, panels, navigation glass | All screens |
| 5 | [Shared content / hero morph](#5-shared-content--hero-morph) | Media album art | [media.md](./media.md) |
| 6 | [Directional content transitions](#6-directional-content-transitions) | Climate, Media, Navigation, Vehicle | [climate.md](./climate.md), [media.md](./media.md), [navigation.md](./navigation.md), [vehicle.md](./vehicle.md) |
| 7 | [Morphing segmented controls](#7-morphing-segmented-controls) | Climate airflow, Vehicle drive mode | [climate.md](./climate.md), [vehicle.md](./vehicle.md) |
| 8 | [Icon / state morphing](#8-icon--state-morphing) | Media play/pause | [media.md](./media.md) |
| 9 | [Layout morphing](#9-layout-morphing) | Vehicle drive modes, Media queue | [vehicle.md](./vehicle.md), [media.md](./media.md) |
| 10 | [Dynamic expressive color](#10-dynamic-expressive-color) | Climate, Vehicle | [climate.md](./climate.md), [vehicle.md](./vehicle.md) |
| 11 | [Predictive back](#11-predictive-back) | Back from expanded widget | [dashboard.md](./dashboard.md) |
| 12 | [Glass / layered surfaces](#12-glass--layered-surfaces) | Dashboard, Navigation, detail cards | All screens |
| 13 | [Motion Studio](#13-motion-studio) | Vehicle Sport / Comfort | [vehicle.md](./vehicle.md) |
| 14 | [Driving safety clamp](#14-driving-safety-clamp) | Home driving state | [home.md](./home.md) |

---

## 1. Motion scheme & tokens

**What it is:** M3 Expressive ships a tuned spring system — bouncier spatial motion and smoother effects than Standard.

**In the demo:**
- Default on home side panel → **Expressive**
- Also compare **Standard** and **Custom** (OEM-tuned)
- **Driving state** forces Standard for safety

**What to say:** *“Expressive isn’t one animation — it’s a system of six motion tokens wired through `MaterialTheme.motionScheme`.”*

| Token | Role | Demo example |
|-------|------|--------------|
| `defaultSpatialSpec()` | Layout, position, size | Column weights, temperature slide, play/pause |
| `slowSpatialSpec()` | Fluid, deliberate moves | Segmented pills, fan bars, queue panel |
| `fastSpatialSpec()` | Quick spatial feedback | Motion Studio “Fast” cell |
| `defaultEffectsSpec()` | Color, opacity, fade | Theme tints, route steps toggle |
| `fastEffectsSpec()` | Snappy effects | Motion Studio “Snappy” cell |
| `slowEffectsSpec()` | Gentle effects | Motion Studio “Gentle” cell |

**Where to show:** Home side panel → Vehicle **Motion Studio**

**Source:** `AppMotionScheme.kt`, `MotionSchemeFactory.kt`, `CustomMotionScheme.kt`

---

## 2. Container transform

**What it is:** A card morphs into a full-screen surface. Bounds, icon, title, and content stay visually connected — no hard cut.

**In the demo:**
- Tap any dashboard widget → detail screen
- Back / predictive back reverses the morph

**Implementation:** `SharedTransitionLayout` + `sharedBounds` + `sharedElement`

**Shared elements per widget:**

| Element | Key pattern |
|---------|-------------|
| Card shell | `widget_<name>` |
| Icon | `widget_<name>_icon` |
| Title | `widget_<name>_title` |
| Main content | `widget_<name>_content` |
| Controls row | `widget_<name>_controls` |

**What to say:** *“This is Material’s container transform — continuity from widget to full screen. Climate and Media are the richest examples.”*

**Where to show:** Dashboard → tap Climate or Media → Back

**Source:** `WidgetSharedTransition.kt`, `IviDemoScreen.kt`

---

## 3. Shape morphing

**What it is:** Corner radii animate between states instead of snapping. A core M3 Expressive idea: shapes feel alive, not static.

**In the demo:**

| Surface | Trigger | Morph |
|---------|---------|-------|
| Widget cards (grid) | A/C on / playing / Sport+charge | Rest → active asymmetric corners |
| Climate dial | A/C toggle | Compact circle → expanded asymmetric |
| Media album art | Play/pause | Compact → expanded album shape |
| Vehicle gauge | Sport / charging | Rest → sport asymmetric |
| Detail cards | A/C on, Sport mode | Rest → expanded card radii |

**Token:** `defaultSpatialSpec()` or `slowSpatialSpec()` via `rememberMorphingRoundedShape`

**What to say:** *“Expressive shape isn’t decoration — it communicates state. Playing media literally reshapes the card.”*

**Where to show:** Dashboard grid (toggle states) → open detail and toggle again

**Source:** `MorphingRoundedShape.kt`

---

## 4. Expressive shape tokens

**What it is:** M3 Expressive uses larger, asymmetric corner radii across the design system shape scale.

**In the demo:** `ExpressiveShapes` (12–48 dp) on cards, panels, navigation glass, queue panel, detail surfaces

| Token | Radius |
|-------|--------|
| extraSmall | 12 dp |
| small | 20 dp |
| medium | 28 dp |
| large | 36 dp |
| extraLarge | 48 dp |

**What to say:** *“We use M3 Expressive shape tokens globally — not just on animated surfaces.”*

**Source:** `ExpressiveShapes.kt`

---

## 5. Shared content / hero morph

**What it is:** A specific element (not just the container) morphs between two layouts using a shared transition key.

**In the demo:**
- **Album art** — same `album_art` key in grid thumbnail and detail hero
- **Widget icon, title, controls** — persist across container transform

**What to say:** *“Shared elements carry identity across the transition — album art grows in place rather than popping in.”*

**Where to show:** Media widget → open detail (watch album art scale)

**Source:** `WidgetEmbeddedContent.kt`, `MediaPlayerScreen.kt`

---

## 6. Directional content transitions

**What it is:** Content swaps with directional motion so change feels intentional (`AnimatedContent`).

**In the demo:**

| Screen | Trigger | Motion |
|--------|---------|--------|
| Climate | +/- temperature | Vertical slide (up = increase) |
| Media | Next/prev track | Slide + fade |
| Navigation | Next maneuver | Scale in/out + fade |
| Vehicle | Odometer change | Vertical slide |
| Vehicle | Drive mode banner | Cross-fade |

**What to say:** *“Directional motion encodes meaning — hotter goes up, colder goes down.”*

**Source:** `AnimatedTemperatureCounter.kt`, `MediaDetailComponents.kt`, `NavigationComponents.kt`, `VehicleEnergyCockpit.kt`

---

## 7. Morphing segmented controls

**What it is:** Selection indicator slides between segments — a pill morph, not an instant swap.

**In the demo:**
- Climate **Airflow** (Face / Feet / Defrost)
- Vehicle **Drive mode** (Eco / Comfort / Sport)

**Token:** `slowSpatialSpec()`

**What to say:** *“Classic expressive pattern — the selection surface travels, it doesn’t blink.”*

**Source:** `MorphingAirflowSegmentedButton.kt`, `MorphingDriveModeSelector.kt`

---

## 8. Icon / state morphing

**What it is:** Icons and controls morph between states rather than swapping instantly.

**In the demo:**
- Media **Play ↔ Pause** — scale + cross-fade between glyphs
- Button shell scales to 0.96× when paused

**Token:** `defaultSpatialSpec()`

**What to say:** *“State changes on primary controls get spatial treatment, not just icon swaps.”*

**Source:** `MorphingPlayPauseButton.kt`

---

## 9. Layout morphing

**What it is:** The layout itself reflows with spring physics — not just individual elements.

**In the demo:**

| Surface | Trigger | Motion |
|---------|---------|--------|
| Vehicle columns | Drive mode change | Animated `weight()` values |
| Vehicle selector | Eco vs Comfort/Sport | Y position slides top ↔ bottom |
| Vehicle side panel | Drive mode | `animateContentSize` + expand/shrink |
| Media queue | Toggle queue | Panel width 0 → 400 dp |

**Tokens:** `defaultSpatialSpec()`, `slowSpatialSpec()`, `defaultEffectsSpec()` (fade)

**What to say:** *“Expressive motion applies to layout, not just micro-interactions. Sport mode literally reshapes the screen.”*

**Where to show:** Vehicle → Eco → Comfort → Sport

**Source:** `VehicleScreen.kt`, `VehicleDriveModeLayout.kt`, `MediaPlayerScreen.kt`

---

## 10. Dynamic expressive color

**What it is:** Color scheme shifts with context, animated via effects tokens.

**In the demo:**

| Screen | Driver | Effect |
|--------|--------|--------|
| Climate | Temperature | Cool → warm palette cross-fade |
| Vehicle | Drive mode + battery + charging | Theme tint shifts |
| Vehicle | Battery level | Gauge arc color (green → amber → red) |
| Vehicle | Charging | Cyan pulse + flow strip color |

**Token:** `defaultEffectsSpec()`

**What to say:** *“Color is an effect channel — it transitions smoothly without moving layout.”*

**Source:** `DynamicClimateColor.kt`, `VehicleDynamicColor.kt`

---

## 11. Predictive back

**What it is:** Android predictive back previews the return destination during the gesture.

**In the demo:** Back from any expanded widget previews the dashboard collapse before committing.

**What to say:** *“Motion continuity extends to system navigation — you peek at the grid before committing back.”*

**Source:** `IviDemoScreen.kt` (`PredictiveBackHandler`)

---

## 12. Glass / layered surfaces

**What it is:** Expressive automotive UI uses layered translucent surfaces for depth.

**In the demo:**
- `glassSurfaceColor()` on dashboard cards and detail surfaces
- Navigation map overlays with glass panels
- Elevated detail cards (`DetailSurfaceCard`)

**What to say:** *“Expressive isn’t only motion — layered surfaces and larger radii create depth on the dashboard.”*

**Source:** `CarBackgroundTokens.kt`, `DetailSurfaceCard.kt`

---

## 13. Motion Studio

**What it is:** A built-in sandbox comparing all six motion tokens side by side.

**In the demo:** Vehicle screen → Comfort or Sport → right column

**How to demo:**
1. Switch to **Sport** or **Comfort** drive mode
2. Tap **Replay**
3. Tap each token cell (Spatial, Fast, Slow, Effects, Snappy, Gentle)
4. Switch scheme chips (Standard / Expressive / Custom) — local `MaterialTheme.motionScheme` override

**What to say:** *“This is our teaching tool — same six tokens Material 3 defines, with Standard vs Expressive vs Custom schemes.”*

**Source:** `VehicleMotionStudio.kt`

---

## 14. Driving safety clamp

**What it is:** Context-aware motion reduction for automotive safety.

**In the demo:** Home → set **Driving** or **Restricted** → all motion forced to **Standard**

**What to say:** *“Expressive when parked; Standard when driving. Motion follows UX safety policy.”*

**Source:** `AppMotionScheme.kt` (`resolveMotionScheme`)

---

## Suggested presentation order (feature-first)

| # | Feature | Show |
|---|---------|------|
| 1 | Motion scheme + tokens | Home panel |
| 2 | Expressive shapes | Dashboard cards |
| 3 | Container transform | Tap Climate |
| 4 | Shape morphing | Toggle A/C |
| 5 | Directional content | Change temperature |
| 6 | Segmented control morph | Airflow selector |
| 7 | Shared hero element | Media album art |
| 8 | Icon morph | Play/pause |
| 9 | Layout morph | Vehicle drive modes |
| 10 | Dynamic color | Climate temp / Vehicle Sport |
| 11 | Motion Studio | Vehicle Sport mode |
| 12 | Safety clamp | Driving state on home |

---

## Feature → screen map

| Feature | Climate | Media | Navigation | Vehicle | Dashboard |
|---------|:-------:|:-----:|:----------:|:-------:|:---------:|
| Container transform | ✓ | ✓ | ✓ | ✓ | ✓ |
| Shape morphing | ✓ | ✓ | — | ✓ | ✓ |
| Shared elements | ✓ | ✓ | ✓ | ✓ | ✓ |
| Directional content | ✓ | ✓ | ✓ | ✓ | — |
| Segmented morph | ✓ | — | — | ✓ | — |
| Icon morph | — | ✓ | — | — | — |
| Layout morph | — | ✓ | — | ✓ | — |
| Dynamic color | ✓ | — | — | ✓ | — |
| Motion Studio | — | — | — | ✓ | — |
