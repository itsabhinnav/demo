package com.test.design.presentation.assistant.gallery

/**
 * Catalog of semi-transparent voice-assistant chrome styles.
 */
enum class AssistantUiStyle(
    val title: String,
    val blurb: String,
) {
    VoicePlate(
        title = "Voice plate",
        blurb = "Bottom plate with face + prompt",
    ),
    FaceOnly(
        title = "Face only",
        blurb = "No plate — floating persona",
    ),
    WaveformCenter(
        title = "Waveform",
        blurb = "Centered Siri-style ribbons",
    ),
    OrbGlow(
        title = "Orb",
        blurb = "Floating orb with ambient bloom",
    ),
    CapsuleFace(
        title = "Capsule face",
        blurb = "Wide geometric glyph capsule",
    ),
    StatusBar(
        title = "Bar",
        blurb = "Thin status strip + meter",
    ),
    SideRail(
        title = "Side rail",
        blurb = "Trailing translucent rail",
    ),
    EqualizerBars(
        title = "Equalizer",
        blurb = "Vertical bar equalizer",
    ),
    ListeningRings(
        title = "Listening rings",
        blurb = "Concentric attention rings",
    ),
    CornerBubble(
        title = "Corner bubble",
        blurb = "Compact bottom-end chip",
    ),
    WaveFaceCombo(
        title = "Wave + face",
        blurb = "Persona over waveform",
    ),
    AmbientPill(
        title = "Ambient pill",
        blurb = "Minimal floating pill HUD",
    ),
    ImmersiveEyes(
        title = "Immersive eyes",
        blurb = "Full-screen gradient · eyes · transcript",
    ),
    DroidFace(
        title = "Droid face",
        blurb = "Expressive shell morph · Bugdroid glyphs",
    ),
    EporoFace(
        title = "EPORO",
        blurb = "Material Gem shell · Bézier visor · glow eyes · LED bar",
    ),
}
