# Material 3 Expressive Sample — Motion Guide

Use this guide while presenting the **Material 3 Expressive Sample** from the AAOS Playground home screen. Each screen doc maps to a visible interaction in the demo and explains **what moves**, **which motion token drives it**, and **what to say** while showing it.

---

## Screen guides

| Screen | Doc | Highlights |
|--------|-----|------------|
| Home (setup) | [docs/motion/home.md](docs/motion/home.md) | Motion scheme chips, driving override, token vocabulary |
| Dashboard hub | [docs/motion/dashboard.md](docs/motion/dashboard.md) | Container transform, predictive back, live card shapes |
| Climate | [docs/motion/climate.md](docs/motion/climate.md) | Temperature slide, dial morph, airflow segmented control |
| Media | [docs/motion/media.md](docs/motion/media.md) | Shared album art, play/pause morph, queue panel |
| Navigation | [docs/motion/navigation.md](docs/motion/navigation.md) | Turn instruction swap, route steps fade |
| Vehicle | [docs/motion/vehicle.md](docs/motion/vehicle.md) | Layout morph, battery gauge, Motion Studio |

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

## Suggested presentation script (5–8 minutes)

| Step | Screen | Action | Talking point |
|------|--------|--------|---------------|
| 1 | [Home](docs/motion/home.md) | Confirm **Expressive** motion scheme, **Parked** driving | Default expressive tuning; schemes are global |
| 2 | [Dashboard](docs/motion/dashboard.md) | Open sample → pause on grid | Cards preview live state — shapes, counters, controls |
| 3 | [Climate](docs/motion/climate.md) | Toggle A/C or open Climate | Shape morph + shared transform |
| 4 | [Climate](docs/motion/climate.md) | Change temperature, airflow, fan | Spatial vs slow spatial vs effects |
| 5 | [Media](docs/motion/media.md) | Play/pause, next track, queue | Shared album art; icon morph; panel slide |
| 6 | [Navigation](docs/motion/navigation.md) | Tap turn card, toggle Steps | Content swap scale; effects fade |
| 7 | [Vehicle](docs/motion/vehicle.md) | Cycle Eco → Comfort → Sport | Full layout morph; highlight Motion Studio in Sport |
| 8 | [Home](docs/motion/home.md) | Set **Driving** → reopen sample | Motion locked to Standard — safety story |

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
