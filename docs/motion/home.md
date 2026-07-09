# Home Screen — Motion Setup

Configure global motion before opening the **Material 3 Expressive Sample**.

[← Back to master guide](../../motion.md) · [Expressive features](./expressive-features.md)

---

## Motion scheme selector (side panel)

The home screen side panel lets you switch between three global motion schemes:

| Scheme | Source | Character |
|--------|--------|-----------|
| **Standard** | `MotionScheme.standard()` | Subtle, restrained springs — baseline Material 3 |
| **Expressive** | `MotionScheme.expressive()` | Bouncier spatial springs — default for this demo |
| **Custom** | `CustomMotionScheme` | OEM-tuned: snappier spatial, critically damped effects |

**Driving state override:** When Driving or Restricted is selected, motion is forced to **Standard** regardless of the chip you pick.

**What to say:** *“In motion, we clamp to Standard for safety and reduced distraction.”*

---

## Motion token vocabulary

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

## Source files

| Area | Files |
|------|-------|
| Motion schemes | `app/.../core/motion/AppMotionScheme.kt`, `theme/MotionSchemeFactory.kt`, `theme/CustomMotionScheme.kt` |
| Home UI | `app/.../presentation/home/HomeScreen.kt` |
| Theme wiring | `app/.../theme/AppTheme.kt` |
