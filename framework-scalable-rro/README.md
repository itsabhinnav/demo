# Framework Adaptive Space RRO

Targets `android` (framework). Toggles:

```xml
<bool name="config_remoteInsetsControllerControlsSystemBars">true</bool>
```

Use with `:scalable-ui-rro` when floating SystemBars need the remote-insets
handshake for Scalable UI SafeBounds. Does **not** zero bar heights or replace
Scalable UI panels.

Android 17 Advanced Windowing removes legacy developer overrides that restricted
app resizing and orientations. Pair this RRO with TaskPanel hosts that declare
`resizeableActivity=true` and `configChanges` covering size/layout so split
resize stays zero-stutter via `onConfigurationChanged`.

Prebuilt: `prebuilt/DesignFrameworkScalableUiRRO.apk`
