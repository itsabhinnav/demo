# CarSystemUI follow-ups (RRO cannot do these)

This RRO seals **panel geometry + hosts**. These items need CarSystemUI (or product)
code / layouts — do them when you work in the SystemUI repo.

## Must-do for floating SystemBars

1. **Disable legacy CarSystemBarPanel**  
   RRO already sets `config_enableTop/Bottom/Left/RightSystemBar=false`.  
   Confirm product SystemUI does not re-enable legacy bars or inflate
   `TopCarSystemBar` / `BottomCarSystemBar` IDs (reserved / legacy).

2. **Glass bar layouts for `status` / `nav`**  
   Scalable UI `<SystemBar id="status|nav">` still needs View suppliers:
   - Dagger `@StringKey("status")` / `@StringKey("nav")` →
     `CarSystemBarViewSupplierUsingLayout` + window supplier
   - Layouts matching Design Compose floating chrome (icons, time, Driver,
     HVAC ±, dock, mic) — port from
     `app/.../FloatingSystemBars.kt` visually
   - Transparent / rounded window background so map shows in bar corners

3. **SystemBar inset / corner clipping**  
   RRO sets `Corner` + side `leftOffset`/`rightOffset`. Verify
   `CarSystemBarWindow` clips content to rounded bounds and does not force
   opaque full-bleed behind the bar.

4. **`window_states` ownership**  
   Product must load this RRO’s `array/window_states` (or merge the same
   panel list). On Dewd today, Dynamic RRO owns `window_states`; a mutable
   Design RRO cannot add Dewd-only XML names via idmap until those names
   exist on base CarSystemUI.

## Must-do for map + widgets

5. **Map host**  
   Controllers point at Design `MapActivity`. Swap
   `string/default_map_activity` to Google Maps / Maps placeholder when ready.
   Ensure `MapsPanelController` accepts the component and `geo:` /
   `NAVIGATE` update filter.

6. **Transparent TaskPanel windows**  
   Widget rail uses `Theme.Design.Panel` (app-side). SystemUI must not force
   an opaque task surface behind floating panels.

7. **Home / StubCarLauncher**  
   Scalable UI home should not fight a full-screen launcher. Prefer Dewd /
   StubCarLauncher + `_System_OnHomeEvent` restoring `map_panel` +
   `widget_panel` opened variants (already in panel XML).

## Nice-to-have

8. Split `widget_panel` into per-glanceable TaskPanels / DecorPanels
   (media, driving status, apps, climate) if you want independent animate /
   hide — RRO can add more `@xml/*` once base SystemUI declares the names.

9. DecorPanel shadows / scrims between map and widgets (MultiPanelLandscapeRRO
   pattern) if glass needs depth beyond TaskPanel corner radius.

10. Framework: keep `:framework-scalable-rro`
    (`config_remoteInsetsControllerControlsSystemBars`) if remote insets
    handshake is required for floating bars.

## Dewd interim (until above lands)

Use `scripts/patch_dewd_fullpower.py` against stock
`DewdDynamicAospRRO.apk` so device keeps Dewd `status`/`nav` while map goes
full-bleed and widget floats at layer 20. That is a bridge, not the sealed
product path.
