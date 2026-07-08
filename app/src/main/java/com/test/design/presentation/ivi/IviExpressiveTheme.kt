package com.test.design.presentation.ivi

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import com.test.design.theme.CarTypography
import com.test.design.theme.ExpressiveShapes

/**
 * Wraps IVI demo screens with Material 3 Expressive motion and typography.
 */
@Composable
fun IviExpressiveTheme(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CarTypography,
        shapes = ExpressiveShapes,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}
