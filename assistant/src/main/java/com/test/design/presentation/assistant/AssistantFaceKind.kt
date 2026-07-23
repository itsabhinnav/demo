package com.test.design.presentation.assistant

/**
 * Swappable persona for the immersive assistant stage.
 *
 * Set via adb — see [AssistantFaceReceiver] / [AssistantFaceConfig]:
 * ```
 * adb shell am broadcast -a com.test.design.action.SET_ASSISTANT_FACE \
 *   -n com.test.design/.presentation.assistant.AssistantFaceReceiver \
 *   --es face fusion
 * ```
 */
enum class AssistantFaceKind(
    /** Canonical token for adb / Settings.Global. */
    val adbKey: String,
    val label: String,
) {
    None(
        adbKey = "none",
        label = "No face",
    ),
    ImmersiveEyes(
        adbKey = "eyes",
        label = "Immersive eyes",
    ),
    ImmersiveGlow(
        adbKey = "glow",
        label = "Immersive glow",
    ),
    Eporo(
        adbKey = "eporo",
        label = "EPORO",
    ),
    Fusion(
        adbKey = "fusion",
        label = "Fusion",
    ),
    FusionGlow(
        adbKey = "fusionglow",
        label = "Fusion glow",
    ),
    FusionEyes(
        adbKey = "fusioneyes",
        label = "Fusion eyes",
    ),
    Droid(
        adbKey = "droid",
        label = "Droid",
    ),
    Glyph(
        adbKey = "glyph",
        label = "Glyph",
    ),
    ;

    companion object {
        val Default: AssistantFaceKind = Fusion

        /** Accepts canonical keys plus common aliases (`off`, `immersive`, `classic`, …). */
        fun parse(raw: String?): AssistantFaceKind? {
            val key = raw?.trim()?.lowercase().orEmpty()
            if (key.isEmpty()) return null
            entries.firstOrNull { it.adbKey == key }?.let { return it }
            entries.firstOrNull { it.name.equals(key, ignoreCase = true) }?.let { return it }
            return when (key) {
                "off", "noface", "no_face", "hidden", "blank" -> None
                "immersive", "immersive_eyes", "eye", "orb" -> ImmersiveEyes
                "immersive_glow", "glow_eyes", "aura", "ring", "purple_eyes" -> ImmersiveGlow
                "eporp", "robot" -> Eporo
                "express", "hybrid", "eporo_immersive", "eporo_eyes" -> Fusion
                "fusion_glow", "glow_fusion", "fusion_capsule", "capsule_fusion" -> FusionGlow
                "fusion_eyes", "eyes_fusion", "fusion_immersive", "fusion_black" -> FusionEyes
                "bugdroid", "android" -> Droid
                "classic", "assistant", "face" -> Glyph
                else -> null
            }
        }
    }
}
