package com.test.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Wraps [MaterialTheme] with a bold OEM brand identity for customized component demos.
 */
@Composable
fun OemBrandTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OemBrandColorScheme,
        typography = OemBrandTypography,
        shapes = OemBrandShapes,
        motionScheme = CustomMotionScheme,
        content = content,
    )
}
