# Framework Scalable UI RRO

Targets `android` (framework). Only toggles:

```xml
<bool name="config_remoteInsetsControllerControlsSystemBars">true</bool>
```

Use with `:scalable-ui-rro` when floating SystemBars need the remote-insets
handshake. Does **not** zero bar heights or replace Scalable UI panels.
