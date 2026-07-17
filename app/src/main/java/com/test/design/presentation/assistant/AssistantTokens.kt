package com.test.design.presentation.assistant

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Production tokens for the assistant panel — restrained Google-like surfaces.
 */
internal object AssistantTokens {
    val PanelWidth = 400.dp
    val PanelCorner = 28.dp
    val PanelInset = 12.dp
    val ContentPadding = 28.dp

    val Surface = Color(0xF2141820)
    val SurfaceTop = Color(0xF01C222C)
    val SurfaceBottom = Color(0xF00E1116)
    val Hairline = Color.White.copy(alpha = 0.08f)

    val OnSurface = Color(0xFFF1F3F4)
    val OnSurfaceVariant = Color(0xFF9AA0A6)
    val OnSurfaceMuted = Color(0xFF80868B)

    val Accent = Color(0xFF8AB4F8)
    val Scrim = Color.Black.copy(alpha = 0.22f)

    const val EnterMs = 400
    const val ExitMs = 280
    const val CrossfadeMs = 320
}
