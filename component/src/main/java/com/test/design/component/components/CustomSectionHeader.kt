package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing

@Composable
fun CustomSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.sm),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = OemOnSurface,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.xs),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.sm)
                .height(1.dp)
                .background(OemBorder),
        )
    }
}
