# Assistant module — standalone extraction

The immersive assistant is split so UI/session chrome never depends on IVI vehicle code.

## Modules

| Module | Role |
|--------|------|
| `:assistant-api` | Pure contracts: `AssistantBackend`, `AssistantHost`, session events/models. No Compose. |
| `:assistant` | Face UI, overlay service, demo backend, STT/TTS adapters, UI gallery. Depends only on `:assistant-api`. |
| `:app` | Implements `AssistantHost` (`DesignAssistantHost`), installs runtime in `DesignApplication`. |

## Wiring

```kotlin
AssistantRuntime.install(
    host = DesignAssistantHost(app),
    backend = DemoAssistantBackend(speakingTts = platformAssistantTts(app)),
)
```

`ImmersiveAssistantOverlay` collects `AssistantBackend.events` and forwards mic input via `onSpeechInput`. Swap `DemoAssistantBackend` for a remote/LLM client without touching Compose.

Publish cabin facts from the host:

```kotlin
DesignCabinContextStore.publish(
    AssistantCabinContext(speedMph = 42, gear = "D", batteryPercent = 78),
)
```

## Keep decoupled

- Do not import vehicle ViewModels from `:assistant`.
- Cabin facts cross the boundary only as `AssistantCabinContext` strings/numbers.
- Theme via `AssistantTheme` inside the assistant module.
