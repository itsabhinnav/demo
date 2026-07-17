package com.test.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object CarBackgroundTokens {
    /** Automotive dark gray canvas (matches `background_image_lhd.png`). */
    val CanvasDark = Color(0xFF1C1E22)

    /** Soft light canvas for daytime / light theme. */
    val CanvasLight = Color(0xFFF0F2F5)

    @Deprecated("Use CanvasDark", ReplaceWith("CanvasDark"))
    val CanvasGray = CanvasDark

    const val TopBarAlpha = 0.86f
    const val GlassSurfaceAlpha = 0.90f
    const val GlassPanelAlpha = 0.82f
    const val DetailTintAlpha = 0.42f
    const val DetailOverlayAlpha = 0.34f
    const val MediaOverlayAlpha = 0.30f
    const val NavigationScrimAlpha = 0.38f
    const val NavigationPanelAlpha = 0.90f
}

@Composable
fun carTopAppBarColors() = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(
        alpha = CarBackgroundTokens.TopBarAlpha,
    ),
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
)

@Composable
fun glassSurfaceColor(): Color =
    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = CarBackgroundTokens.GlassSurfaceAlpha)

@Composable
fun glassPanelColor(): Color =
    MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = CarBackgroundTokens.GlassPanelAlpha)

@Composable
fun navigationGlassPanelColor(): Color =
    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = CarBackgroundTokens.NavigationPanelAlpha)
