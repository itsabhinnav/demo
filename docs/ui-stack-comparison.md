# UI Stack Comparison — Material Theme & View Systems

A tabular reference comparing how Android UI stacks handle theming, layout, components, motion, and automotive (AAOS) concerns. Use this when choosing between legacy Views, hybrid interop, or a Compose-first architecture.

[← Back to master motion guide](../motion.md)

---

## Approaches at a glance

| Approach | Layout | Theme system | Component model | Typical use |
|----------|--------|--------------|-----------------|-------------|
| **A — Material theme + legacy Views (XML)** | `res/layout/*.xml` | `themes.xml`, `styles.xml`, `attrs` | Material Components for Views (`MaterialButton`, `MaterialCardView`, …) | Existing OEM apps, XML-heavy codebases |
| **B — Legacy Views + Compose (hybrid)** | XML + `@Composable` via `ComposeView` / `AndroidView` | Split: XML theme for Activities + `MaterialTheme` in Compose islands | View widgets wrapped in Compose or vice versa | Incremental migration |
| **C — Compose screens + stock M3 components** | `@Composable` functions | `MaterialTheme` (`colorScheme`, `typography`, `shapes`, `motionScheme`) | Material 3 Compose primitives (`Button`, `Card`, `Slider`, …) | Greenfield apps, design-system demos |
| **D — Compose screens + custom Compose components** | `@Composable` trees | Layered themes (`AppTheme` → feature sub-themes → OEM brand) | Domain composables built on M3 tokens (`DashboardWidgetCard`, morphing controls, …) | Product UI with expressive motion and OEM branding |

**This repo today:** **D** for all in-app UI. XML is limited to the activity window shell (`Theme.Design` in `themes.xml`); screens and components are Compose-only. See [How this demo maps](#how-this-demo-maps) below.

---

## Theming & design tokens

| Capability | A — XML + Material Views | B — Hybrid | C — Compose + M3 | D — Compose + custom components |
|------------|--------------------------|------------|------------------|--------------------------------|
| Color roles (`primary`, `surfaceContainer`, …) | Via `Theme.MaterialComponents` color attributes; M2-style mapping | Duplicated: XML attrs + `ColorScheme` | Full M3 `ColorScheme` in `MaterialTheme` | Inherited `ColorScheme` + per-feature overrides (e.g. dynamic climate tint) |
| Typography scale | `TextAppearance.*` styles in XML | Two systems to keep in sync | `Typography` composable tokens (`displayLarge`, `bodyLarge`, …) | AAOS-scaled typography (`CarTypography`, 24sp minimum body) |
| Shape tokens | `shapeAppearance*` on Material widgets | Partial overlap | `Shapes` in `MaterialTheme` | Expressive shapes, morphing shapes, OEM brand shapes |
| Motion tokens | View property animators, `MotionLayout` (separate from M3 motion scheme) | Fragment/View animators + Compose `MotionScheme` | `MaterialTheme.motionScheme` (Standard / Expressive) | Custom motion factory, driving-state clamp to Standard |
| Dynamic / context-driven color | Limited (e.g. `MaterialColors`, manual tint) | Manual bridge between systems | `dynamicDarkColorScheme`, local `MaterialTheme(colorScheme = …)` | `VehicleDynamicColor`, `DynamicClimateColor`, `CompositionLocal` driving state |
| Theme nesting / scoping | Activity / context theme overlays | Activity theme + `CompositionLocalProvider` | `MaterialTheme` wrapper composables | `AppTheme` → `IviExpressiveTheme` → `OemBrandTheme` → screen-local scheme |
| Dark / light mode | `values-night/`, `uiMode` | Both stacks must handle `uiMode` | `isSystemInDarkTheme()` | Single dark automotive canvas; state-driven UX modes instead |
| Window / system chrome | Full control via XML theme | XML for window; Compose for insets | `enableEdgeToEdge`, `WindowInsets` | Same as C; `Theme.Design` sets `windowBackground` only |

---

## Layout & structure

| Capability | A — XML + Material Views | B — Hybrid | C — Compose + M3 | D — Compose + custom components |
|------------|--------------------------|------------|------------------|--------------------------------|
| Layout definition | XML (`ConstraintLayout`, `LinearLayout`, …) | Mixed XML and Compose | Composable layout (`Column`, `Row`, `Box`, `LazyColumn`) | Feature screens + `components/` packages |
| State binding | ViewBinding / DataBinding / manual `findViewById` | Binding in Views; `remember` / `State` in Compose | `State`, `ViewModel` + `collectAsStateWithLifecycle` | MVI `ViewModel` + `StateFlow` per feature |
| Lists | `RecyclerView` + adapter | `RecyclerView` or `LazyColumn` | `LazyColumn` / `LazyVerticalGrid` | `LazyVerticalGrid` with shared transitions (dashboard) |
| Navigation | Fragments + Navigation Component (XML graphs) | Fragment transitions + Compose `NavHost` | Navigation Compose | `AppNavHost`, typed `AppDestination` routes |
| Configuration changes | `onSaveInstanceState`, retained Fragments | Split responsibility | `ViewModel`, `rememberSaveable` | ViewModels + composition locals (`DrivingUxState`) |
| Preview / design-time | Layout Editor, limited fidelity | Partial (Compose Preview for islands) | `@Preview` composables | AAOS landscape previews (1920×720) |
| Accessibility | Content descriptions in XML, `importantForAccessibility` | Must align across both stacks | Semantics API, `Modifier.semantics` | AAOS touch targets via `carTouchTarget()`, list heights |

---

## Components & styling

| Capability | A — XML + Material Views | B — Hybrid | C — Compose + M3 | D — Compose + custom components |
|------------|--------------------------|------------|------------------|--------------------------------|
| Button, chip, slider, etc. | `com.google.android.material.*` View widgets | Duplicate APIs (View vs Compose) | `androidx.compose.material3.*` | Stock M3 + OEM-styled variants (`*Defaults`, `ShowcaseSectionStyle`) |
| Cards & surfaces | `MaterialCardView`, elevation | Elevation (View) vs tonal surfaces (Compose) | `Card`, `Surface`, tonal `surfaceContainer*` | Glass surfaces (`CarBackgroundTokens`, `DetailSurfaceCard`) |
| Custom widgets | Custom `View` subclasses | `AndroidView` factory or custom Composable | Custom `@Composable` | Morphing controls (`MorphingPlayPauseButton`, segmented buttons) |
| Style reuse | Styles, themes, drawables | Styles + `MaterialTheme` extension | Theme tokens + modifier chains | `CarModifiers`, `CarDesignTokens`, shared `components/` |
| Material 3 Expressive | Not available on View Material Components | Expressive only in Compose islands | Full M3 Expressive APIs | Container transform, shared element, shape morph, layout morph |
| Dependency surface | AppCompat, Material Views, ConstraintLayout | Both dependency graphs | Compose BOM, Material3, Navigation Compose | Same as C + internal theme/component packages |

---

## Motion & animation

| Capability | A — XML + Material Views | B — Hybrid | C — Compose + M3 | D — Compose + custom components |
|------------|--------------------------|------------|------------------|--------------------------------|
| M3 motion scheme (springs) | ❌ | ✅ in Compose regions only | ✅ | ✅ with driving clamp |
| Shared element / hero | `TransitionInflater`, Fragment transitions | Awkward across View↔Compose boundary | `SharedTransitionLayout`, `Modifier.sharedElement` | Media album art, dashboard widget expand |
| Container transform | `MaterialContainerTransform` (View) | Mixed APIs | Compose shared transition APIs | Dashboard grid → detail |
| Shape morphing | Limited (`AnimatedVectorDrawable`, custom) | Compose-only for morph | `MorphingRoundedShape`, animated shapes | Climate/media/vehicle morphing panels |
| Predictive back | Fragment / Activity callbacks | Partial coordination | `PredictiveBackHandler`, Compose animations | IVI dashboard back gesture |
| `MotionLayout` (ConstraintLayout) | ✅ primary View tool | ✅ in XML portions | N/A (use Compose animation) | Not used — Compose motion instead |

---

## AAOS / automotive-specific

| Concern | A — XML + Material Views | B — Hybrid | C — Compose + M3 | D — Compose + custom components |
|---------|--------------------------|------------|------------------|--------------------------------|
| Minimum touch target (76dp) | Manual dimens / styles | Enforce in both stacks | `Modifier.sizeIn`, custom modifiers | `carTouchTarget()`, `CarDesignTokens` |
| Typography (24sp+ body) | `TextAppearance` per widget | Duplicate text styles | `Typography` token overrides | `CarTypography` mapped to M3 slots |
| Driving-state UX (motion clamp) | Custom logic in Activities | Hard to keep consistent | `CompositionLocal` + motion resolver | `DrivingUxState`, `resolveMotionScheme()` |
| Landscape / wide display | XML alternative resources | Mixed | `BoxWithConstraints`, grid weights | Dashboard grid, 1920×720 previews |
| Glance / distraction guidelines | Manual enforcement | Risk of inconsistency | Token-driven sizing and motion | Documented in [expressive-features.md](./motion/expressive-features.md) |

---

## Build, test & maintenance

| Aspect | A — XML + Material Views | B — Hybrid | C — Compose + M3 | D — Compose + custom components |
|--------|--------------------------|------------|------------------|--------------------------------|
| Module split (`app` / `component`) | Common for OEM libraries | Common during migration | Compose library modules | Planned `component` module; currently packages in `app` |
| UI unit tests | Robolectric, limited | Split test targets | Compose UI tests, screenshot tests | `@Preview` + MVI unit tests |
| Refactor cost | High coupling to XML IDs | Highest (two paradigms) | Medium | Medium — invest in shared tokens/components |
| Designer ↔ dev handoff | Layout Editor, Figma XML | Fragmented | Compose Preview, Code Connect | Same as C + motion presentation docs |
| New Google M3 features | Lag behind Compose | Compose side advances first | First-class | First-class via theme layer upgrades |

---

## Decision matrix (when to choose what)

| If you need… | Prefer |
|--------------|--------|
| Maintain a large existing XML codebase with minimal churn | **A** or gradual **B** |
| Ship new screens while retiring Fragments | **B** → **C** |
| Material 3 Expressive motion, shared transitions, dynamic color | **C** or **D** |
| OEM brand themes + domain widgets (climate, media, vehicle) | **D** |
| Single source of truth for tokens across products | **D** (extract `component` module) |
| Fastest path to AAOS HIG compliance in greenfield work | **C** with AAOS token overrides (**D** in this repo) |

---

## How this demo maps

| Layer | Implementation in this repo | Stack |
|-------|----------------------------|-------|
| Activity window / splash background | `res/values/themes.xml` → `Theme.Design` | A (shell only) |
| All screens & navigation | `MainActivity` + `AppNavHost` + feature `*Screen.kt` | D |
| Stock Material gallery | `presentation/material/MaterialComponentsScreen.kt` | C |
| OEM-branded gallery | `presentation/material/CustomizedMaterialComponentsScreen.kt` under `OemBrandTheme` | D |
| IVI expressive dashboard | `presentation/ivi/` with `IviExpressiveTheme` | D |
| Reusable primitives | `theme/`, `presentation/ivi/common/`, `presentation/ivi/*/components/` | D |
| Legacy XML layouts, ViewBinding, Fragments | Not present | — |
| Hybrid `ComposeView` / `AndroidView` interop | Not present | — |

---

## Related docs

- [Material 3 Expressive features](./motion/expressive-features.md) — motion and expressive capabilities used in the Compose stack
- [motion.md](../motion.md) — presentation guide and screen index
- [AGENTS.md](../AGENTS.md) — project modules and build commands
