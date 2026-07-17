package com.test.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.test.design.core.theme.LocalAppThemeMode
import com.test.design.core.theme.resolveDarkTheme

/**
 * Wraps [MaterialTheme] with the Horizon OEM brand identity for customized component demos.
 */
@Composable
fun OemBrandTheme(content: @Composable () -> Unit) {
    val darkTheme = LocalAppThemeMode.current.resolveDarkTheme(isSystemInDarkTheme())
    val colorScheme = if (darkTheme) OemBrandDarkColorScheme else OemBrandLightColorScheme
    CompositionLocalProvider(LocalContentColor provides colorScheme.onBackground) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = OemBrandTypography,
            shapes = OemBrandShapes,
            motionScheme = CustomMotionScheme,
            content = content,
        )
    }
}
