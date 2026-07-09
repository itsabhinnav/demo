# Material 3 Expressive Sample — Motion Guide

Use this guide while presenting the **Material 3 Expressive Sample** from the AAOS Playground home screen. Each section maps to a visible interaction in the demo and explains **what moves**, **which motion token drives it**, and **what to say** while showing it.

---

## Before you start

### Motion scheme selector (home side panel)

The home screen side panel lets you switch between three global motion schemes:

| Scheme | Source | Character |
|--------|--------|-----------|
| **Standard** | `MotionScheme.standard()` | Subtle, restrained springs — baseline Material 3 |
| **Expressive** | `MotionScheme.expressive()` | Bouncier spatial springs — default for this demo |
| **Custom** | `CustomMotionScheme` | OEM-tuned: snappier spatial, critically damped effects |

**Driving state override:** When Driving or Restricted is selected, motion is forced to **Standard** regardless of the chip you pick. Say: *“In motion, we clamp to Standard for safety and reduced distraction.”*

### Motion token vocabulary

Material 3 exposes six tokens through `MaterialTheme.motionScheme`:

| Token | Used for | Examples in this demo |
|-------|----------|----------------------|
| `defaultSpatialSpec()` | General position, size, layout | Column weights, temperature slides, play/pause scale |
| `fastSpatialSpec()` | Quick spatial feedback | Motion Studio “Fast” preview cell |
| `slowSpatialSpec()` | Deliberate, fluid spatial moves | Segmented indicators, fan bars, queue panel, gauge shape |
| `defaultEffectsSpec()` | Color, opacity, cross-fades | Theme tints, route steps toggle, health colors |
| `fastEffectsSpec()` | Snappy non-spatial changes | Motion Studio “Snappy” preview cell |
| `slowEffectsSpec()` | Gentle non-spatial changes | Motion Studio “Gentle” preview cell |

**Spatial** = things that move in space (offset, size, layout).  
**Effects** = appearance changes (color, alpha, fade) without changing layout geometry.

---

## Demo flow overview

```
Home → Dashboard grid → Tap widget → Full-screen detail → Back
         ↑                                    ↓
    Live widget previews              Shared element morph
    (shapes, counters, controls)      (container transform)
```

1. Open **Material 3 Expressive Sample** from home.
2. Show the **dashboard widget grid** — four live widgets plus placeholders.
3. Tap any widget to trigger a **container transform** (card → full screen).
4. Interact inside the detail screen to show **in-place motion**.
5. Press **Back** (or predictive back gesture) to collapse with the reverse transform.

---

## 1. Dashboard hub

### 1.1 Container transform (widget open / close)

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

### 1.2 Predictive back

**What:** While a widget is expanded, a back gesture previews the collapse before committing.

**Implementation:** `PredictiveBackHandler` on `IviDemoScreen`; cancels cleanly if the gesture is aborted.

**What to say:** *“Predictive back lets the user peek at the return destination before completing navigation.”*

### 1.3 Live widget shape morphing (grid only)

Cards react to widget state **before** you open them:

| Widget | Trigger | Shape change |
|--------|---------|--------------|
| Climate | A/C on | Rest corners → active asymmetric radii |
| Media | Playing | Rest corners → playing asymmetric radii |
| Vehicle | Sport mode or charging | Rest corners → active asymmetric radii |

**Token:** `defaultSpatialSpec()` via `rememberMorphingRoundedShape`.

**What to say:** *“Expressive shape isn’t static — corner radii morph with state so the card feels alive on the dashboard.”*

---

## 2. Climate widget

### 2.1 Temperature counter (slide)

**Trigger:** Tap +/- or change zone.

**Motion:** `AnimatedContent` with vertical slide — number exits upward when increasing, downward when decreasing (and vice versa).

**Token:** `defaultSpatialSpec()`

**What to say:** *“Digit changes use directional spatial motion so increase vs decrease is readable at a glance.”*

### 2.2 Climate dial shape morph

**Trigger:** Toggle A/C on the detail screen (or from embedded widget state).

**Motion:** Dial corners animate between compact circle-like radii and expanded asymmetric radii.

**Token:** `defaultSpatialSpec()`

### 2.3 Dynamic color tint

**Trigger:** Change temperature — palette shifts cool → warm.

**Motion:** Background gradient primary color cross-fades.

**Token:** `defaultEffectsSpec()`

**What to say:** *“Color is an effect, not a layout change — it uses the effects spring so the tint feels smooth without shifting layout.”*

### 2.4 Zone selector cards

**Trigger:** Tap Driver or Passenger zone.

**Motion:** Selected card background color animates.

**Token:** `defaultSpatialSpec()` (color on spatial channel in this implementation)

### 2.5 Fan speed bars

**Trigger:** Tap a bar or change fan level.

**Motion:** Active bars grow in opacity/height fraction; inactive bars stay dimmed.

**Token:** `slowSpatialSpec()`

**What to say:** *“Fan intensity uses slow spatial — a deliberate, fluid build rather than a snap.”*

### 2.6 Seat heat indicator

**Trigger:** Increase/decrease seat heat.

**Motion:** Active pips scale up; inactive pips shrink to 50%.

**Token:** `defaultSpatialSpec()`

### 2.7 Airflow segmented control

**Trigger:** Select Face / Feet / Defrost.

**Motion:** Primary pill indicator slides horizontally to the selected segment.

**Token:** `slowSpatialSpec()`

**What to say:** *“Segmented controls are a classic expressive pattern — the indicator morphs between segments with slow spatial easing.”*

### 2.8 Detail surface cards (A/C morph)

**Trigger:** A/C on expands airflow and seat-heat cards.

**Motion:** Card corner radii morph between rest and expanded profiles.

**Token:** `defaultSpatialSpec()`

---

## 3. Media widget

### 3.1 Album art shared element

**Trigger:** Open Media from grid.

**Motion:** Album art morphs from compact thumbnail to large hero — same shared key (`album_art`) in grid and detail.

**What to say:** *“Album art is a shared element — it scales and repositions without disappearing and reappearing.”*

### 3.2 Album / card shape morph

**Trigger:** Play or pause.

**Motion:** Album and widget card corners shift between rest and playing radii.

**Token:** `defaultSpatialSpec()`

### 3.3 Track info transition

**Trigger:** Next / previous track.

**Motion:** Title, artist, (album) slide vertically with fade — incoming from below, outgoing upward.

**Tokens:** `defaultSpatialSpec()` (slide) + `defaultEffectsSpec()` (fade)

**What to say:** *“Track changes combine spatial slide with effects fade — spatial for direction, effects for opacity.”*

### 3.4 Play / pause morph

**Trigger:** Tap transport play/pause.

**Motion:**
- Button shell scales slightly when paused (0.96×)
- Icon cross-fades with scale in/out between Play and Pause glyphs

**Token:** `defaultSpatialSpec()`

### 3.5 Queue panel width

**Trigger:** Tap queue icon in transport bar.

**Motion:** Side panel animates between `0.dp` and `400.dp`.

**Token:** `slowSpatialSpec()`

**What to say:** *“Secondary panels slide open with slow spatial so the layout reflow feels intentional, not abrupt.”*

---

## 4. Navigation widget

### 4.1 Turn instruction card

**Trigger:** Tap the card to cycle maneuvers.

**Motion:** Instruction text scales in from 0.9× and fades; outgoing text scales to 1.05× and fades out.

**Token:** `defaultSpatialSpec()`

**What to say:** *“Turn-by-turn updates emphasize the new instruction with a subtle scale pop — spatial spring on a content swap.”*

### 4.2 Route steps panel

**Trigger:** Toggle **Steps** chip in the header.

**Motion:** Steps list fades in/out.

**Token:** `defaultEffectsSpec()`

### 4.3 Emphasized detail card

**What:** The turn card uses `DetailSurfaceCard(emphasized = true)` which applies a slight scale-up (1.02×).

**Token:** `defaultSpatialSpec()`

---

## 5. Vehicle widget

Vehicle is the deepest motion surface — layout, color, and a built-in **Motion Studio**.

### 5.1 Battery gauge

**Triggers:** Change battery %, toggle charging, cycle demo values (tap gauge).

| Property | Token |
|----------|-------|
| Arc fill progress | `slowSpatialSpec()` |
| Range ring progress | `defaultSpatialSpec()` |
| Arc color (charge / level) | `defaultEffectsSpec()` |
| Gauge shape (Sport / charging) | `slowSpatialSpec()` |
| Charging pulse (scale) | Infinite `tween` — decorative loop |
| Background orb drift | Infinite `tween` — ambient motion |

**What to say:** *“Progress arcs use spatial springs; color shifts use effects. Charging adds ambient pulse loops on top of the M3 token system.”*

### 5.2 Drive mode selector

**Trigger:** Eco / Comfort / Sport.

**Motion:** Primary pill slides across three segments (same pattern as climate airflow).

**Token:** `slowSpatialSpec()`

### 5.3 Drive-mode layout rearrangement

**Trigger:** Switch drive mode.

**Motion:** The three-column row **reweights** smoothly:

| Mode | Layout behavior |
|------|-----------------|
| Eco | Energy column widens; Motion Studio hidden; selector moves to top |
| Comfort | Balanced weights; Motion Studio visible below stats |
| Sport | Side column widens; Motion Studio dominates |

**Tokens:**
- Column `weight()` values → `defaultSpatialSpec()`
- Drive selector Y position → `slowSpatialSpec()`
- Systems panel top/bottom padding → `slowSpatialSpec()`
- Side column height → `animateContentSize` with `defaultSpatialSpec()`
- Motion Studio show/hide → `expandVertically` / `shrinkVertically` + fade

**What to say:** *“Drive mode doesn’t just recolor — the whole layout morphs in place. Spatial tokens animate weights and offsets; effects tokens handle fade for panels entering or leaving.”*

### 5.4 Dynamic vehicle color scheme

**Trigger:** Drive mode, battery level, charging.

**Motion:** Screen tint cross-fades.

**Token:** `defaultEffectsSpec()`

### 5.5 Vehicle systems panel

**Triggers:** Select a system, change regen, toggle charging.

| Element | Motion | Token |
|---------|--------|-------|
| Health score badge color | Color cross-fade | `defaultEffectsSpec()` |
| Power flow strip strength | Width fraction | `slowSpatialSpec()` |
| Flow strip color | Color cross-fade | `defaultEffectsSpec()` |
| Flow node alpha | Fade | `defaultEffectsSpec()` |
| System row progress bar | Fill fraction | `defaultSpatialSpec()` |
| Row selection background | Color cross-fade | `defaultEffectsSpec()` |

### 5.6 Trip stats card

**Trigger:** Switch to Sport mode.

**Motion:** Stats card corners morph to expanded radii.

**Token:** `defaultSpatialSpec()`

### 5.7 Odometer counter

**Trigger:** (Demo data updates)

**Motion:** Same directional vertical slide as climate temperature.

**Token:** `defaultSpatialSpec()`

### 5.8 Motion Studio (Sport / Comfort)

**Location:** Vehicle detail → right column (prominent in Sport).

**Purpose:** Live comparison of all six motion tokens on one screen.

**How to demo:**
1. Switch to **Sport** or **Comfort** drive mode.
2. Tap **Replay** to run the preview animation.
3. Tap each token cell (Spatial, Fast, Slow, Effects, Snappy, Gentle).
4. Optionally switch scheme chips (Standard / Expressive / Custom) — Vehicle screen uses a **local** `MaterialTheme.motionScheme` override.

| Cell | Animation shown |
|------|-----------------|
| Spatial | Dot slides horizontally |
| Fast | Dot slides with `fastSpatialSpec()` |
| Slow | Dot slides with `slowSpatialSpec()` |
| Effects | Dot scales in place |
| Snappy | Dot scales with `fastEffectsSpec()` |
| Gentle | Dot scales with `slowEffectsSpec()` |

**What to say:** *“Motion Studio is our sandbox — same six tokens Material 3 defines, side by side. Switch schemes to feel Standard vs Expressive vs our OEM Custom tuning.”*

---

## 6. Placeholder widgets

Energy, Calls, Camera, and Trips use the same **container transform** and shared elements (icon, title, content, controls) but with dummy list content. Motion behavior matches the hub pattern in section 1.

---

## Suggested presentation script (5–8 minutes)

| Step | Action | Talking point |
|------|--------|---------------|
| 1 | Home → confirm **Expressive** motion scheme, **Parked** driving | Default expressive tuning; schemes are global |
| 2 | Open sample → pause on grid | Cards preview live state — shapes, counters, controls |
| 3 | Toggle A/C on Climate card (from grid if wired) or open Climate | Shape morph + shared transform |
| 4 | Change temperature, airflow, fan | Spatial vs slow spatial vs effects |
| 5 | Back → open Media → play/pause, next track, queue | Shared album art; icon morph; panel slide |
| 6 | Back → open Navigation → tap turn card, toggle Steps | Content swap scale; effects fade |
| 7 | Open Vehicle → cycle Eco → Comfort → Sport | Full layout morph; highlight Motion Studio in Sport |
| 8 | Home → set **Driving** → reopen sample | Motion locked to Standard — safety story |

---

## Source file reference

| Area | Primary files |
|------|---------------|
| Shared transitions | `app/.../dashboard/WidgetSharedTransition.kt`, `IviDemoScreen.kt` |
| Shape morphing | `app/.../theme/MorphingRoundedShape.kt` |
| Motion schemes | `app/.../core/motion/AppMotionScheme.kt`, `theme/MotionSchemeFactory.kt`, `theme/CustomMotionScheme.kt` |
| Climate | `app/.../climate/components/*.kt` |
| Media | `app/.../media/components/*.kt`, `MediaPlayerScreen.kt` |
| Navigation | `app/.../navigation/NavigationScreen.kt`, `navigation/components/NavigationComponents.kt` |
| Vehicle | `app/.../vehicle/VehicleScreen.kt`, `vehicle/components/*.kt` |

---

## Quick FAQ while presenting

**Why do some color animations use spatial spec?**  
Compose’s `animateColorAsState` accepts any `FiniteAnimationSpec`. Some call sites pass spatial specs intentionally for a slightly different feel; effects specs are used where only appearance should change.

**Can I compare Standard vs Expressive live?**  
Yes — use the home side panel scheme chips (while Parked), or Vehicle Motion Studio scheme chips for a screen-local override.

**What’s Custom vs Expressive?**  
Expressive is Google’s M3 expressive spring set. Custom is OEM-tuned: medium spatial with varied damping, and critically damped (no-bounce) effects for snappy color/opacity.
