# Climate Screen — Motion

Climate demonstrates directional counters, shape morphing, segmented controls, dynamic color tints, and skeuomorphic HVAC hardware cues.

[← Back to master guide](../../motion.md) · [Dashboard hub](./dashboard.md)

---

## Temperature counter (slide)

**Trigger:** Tap +/- or change zone.

**Motion:** `AnimatedContent` with vertical slide — number exits upward when increasing, downward when decreasing (and vice versa).

**Token:** `defaultSpatialSpec()`

**What to say:** *“Digit changes use directional spatial motion so increase vs decrease is readable at a glance.”*

---

## Climate dial shape morph

**Trigger:** Toggle A/C on the detail screen (or from embedded widget state).

**Motion:** Dial corners animate between compact circle-like radii and expanded asymmetric radii.

**Token:** `defaultSpatialSpec()`

---

## Skeuomorphic dial shell

**Trigger:** Always on for zone temperature dials.

**Look:** Raised metallic bezel, recessed radial face, tick marks, and specular rim highlight — reads as a physical HVAC thermostat knob.

**Source:** `ClimateSkeuomorphism.kt` (`skeuomorphicDialShell`)

---

## Dynamic color tint

**Trigger:** Change temperature — palette shifts cool → warm.

**Motion:** Background gradient primary color cross-fades.

**Token:** `defaultEffectsSpec()`

**What to say:** *“Color is an effect, not a layout change — it uses the effects spring so the tint feels smooth without shifting layout.”*

---

## Zone selector cards

**Trigger:** Tap Driver or Passenger zone.

**Motion:** Selected card background color animates.

**Token:** `defaultSpatialSpec()` (color on spatial channel in this implementation)

---

## Fan speed bars

**Trigger:** Tap a bar or change fan level.

**Motion:** Active bars grow in opacity/height fraction; inactive bars stay dimmed. Bars use raised gloss and depth so they read as physical vent sliders.

**Token:** `slowSpatialSpec()`

**What to say:** *“Fan intensity uses slow spatial — a deliberate, fluid build rather than a snap.”*

---

## Seat heat indicator

**Trigger:** Increase/decrease seat heat.

**Motion:** Active pips scale up; inactive pips shrink to 50%.

**Token:** `defaultSpatialSpec()`

---

## Airflow segmented control

**Trigger:** Select Face / Feet / Defrost.

**Motion:** Primary pill indicator slides horizontally to the selected segment. Track is recessed; pill is a raised hardware switch.

**Token:** `slowSpatialSpec()`

**What to say:** *“Segmented controls are a classic expressive pattern — the indicator morphs between segments with slow spatial easing.”*

---

## Detail surface cards (A/C morph)

**Trigger:** A/C on expands airflow and seat-heat cards.

**Motion:** Card corner radii morph between rest and expanded profiles.

**Token:** `defaultSpatialSpec()`

---

## Source files

| Area | Files |
|------|-------|
| Screen | `app/.../climate/ClimateControlScreen.kt` |
| Components | `app/.../climate/components/AnimatedTemperatureCounter.kt`, `MorphingAirflowSegmentedButton.kt`, `ClimateZoneSelector.kt`, `ClimateTemperatureSection.kt`, `ClimateSkeuomorphism.kt` |
| Shapes | `app/.../theme/MorphingRoundedShape.kt` |
