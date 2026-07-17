# CarSystemUI follow-ups (RRO cannot do these)

This RRO seals **panel geometry + hosts** for Adaptive Space. These items need
CarSystemUI (or product) code / layouts — do them when you work in the SystemUI repo.

## Must-do for floating SystemBars

1. **Disable legacy CarSystemBarPanel**  
   RRO already sets `config_enableTop/Bottom/Left/RightSystemBar=false`.  
   Confirm product SystemUI does not re-enable legacy bars or inflate
   `TopCarSystemBar` / `BottomCarSystemBar` IDs (reserved / legacy).  
   If those flags stay `true`, the UI shows **full-bleed legacy strips** even
   when Scalable UI `<SystemBar>` XML exists.

2. **Glass bar layouts for `status` / `nav`**  
   Scalable UI `<SystemBar id="status|nav">` still needs View suppliers:
   - Dagger `@StringKey("status")` / `@StringKey("nav")` →
     `CarSystemBarViewSupplierUsingLayout` + window supplier
   - Layouts matching Design Compose floating chrome (icons, time, Driver,
     HVAC ±, dock, mic) — port from
     `app/.../FloatingSystemBars.kt` visually
   - Transparent / rounded window background so map shows in bar corners
   - Wire Media / Parking buttons to `_Design_OpenMediaOverlay` /
     `_Design_OpenParking` (see `strings.xml` event ids)

3. **SystemBar inset / corner clipping**  
   RRO sets `Corner radius="28dp"` + `leftOffset`/`rightOffset="12dp"` (literal
   dp — no Design-only dimen names required for idmap). Verify
   `CarSystemBarWindow` clips content to rounded bounds and does not force
   opaque full-bleed behind the bar. Bounds still touch the top/bottom edge
   (Scalable UI requirement); side insets + corner create the floating look.

4. **`window_states` ownership**  
   Product must load this RRO’s `array/window_states` (or merge the same
   panel list including `depth_scrim`, `media_overlay`, `parking_assistant`).
   On Dewd today, Dynamic RRO owns `window_states`; a mutable Design RRO
   cannot add Dewd-only XML names via idmap until those names exist on base
   CarSystemUI.

## Must-do for Adaptive Space panels

5. **Map host**  
   Controllers point at Design `MapActivity`. Swap
   `string/default_map_activity` to Google Maps / Maps placeholder when ready.
   Ensure `MapsPanelController` accepts the component and `geo:` /
   `NAVIGATE` update filter.

6. **Transparent TaskPanel windows**  
   Widget rail + overlays use `Theme.Design.Panel` (app-side). SystemUI must
   not force an opaque task surface behind floating panels.

7. **Home / StubCarLauncher**  
   Scalable UI home should not fight a full-screen launcher. Prefer Dewd /
   StubCarLauncher + `_System_OnHomeEvent` restoring `map_panel` +
   `widget_panel` opened variants (already in panel XML).

8. **DecorPanel depth scrim**  
   `depth_scrim` is declared in the RRO. Confirm CarSystemUI inflates
   DecorPanels (view supplier / empty translucent layer) so Alpha transitions
   render between map and overlays.

9. **Split resize events**  
   Expose `_Design_SplitNarrow|Mid|Wide|Full` / `_Design_CloseSplit` from a
   drag handle or SystemBar control so `app_panel` DynamicVariant /
   discrete variants animate without activity restart.

## Nice-to-have

10. Split `widget_panel` into per-glanceable TaskPanels if you want independent
    animate / hide beyond the combined rail.

11. Keep `:framework-scalable-rro`
    (`config_remoteInsetsControllerControlsSystemBars`) for floating-bar
    SafeBounds handshake.

## Dewd interim (until above lands)

Use `scripts/patch_dewd_fullpower.py` against stock
`DewdDynamicAospRRO.apk` so device:
- keeps Dewd `status`/`nav` SystemBar hosts
- converts those bars from **full-bleed (legacy look)** to **12dp side-inset
  floating bounds**
- makes map full-bleed and widget float at layer 20

Corner radius still needs Design RRO / CarSystemUI window clipping. This is a
bridge, not the sealed product Adaptive Space path.
