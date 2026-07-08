package com.test.design.presentation.ivi

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.test.design.theme.CarTypography
import com.test.design.theme.ExpressiveShapes

/**
 * IVI screens inherit the app-wide [MaterialTheme.motionScheme] from [AppTheme].
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
        motionScheme = MaterialTheme.motionScheme,
        content = content,
    )
}
