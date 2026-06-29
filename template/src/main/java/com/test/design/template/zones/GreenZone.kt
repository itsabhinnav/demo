package com.test.design.template.zones

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemSpacing

@Composable
fun GreenZone(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val showDebugBorder = LocalInspectionMode.current

    Surface(
        modifier = modifier.then(
            if (showDebugBorder) {
                Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            } else {
                Modifier
            },
        ),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(OemSpacing.md),
        ) {
            content()
        }
    }
}
