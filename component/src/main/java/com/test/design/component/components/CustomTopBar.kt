package com.test.design.component.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.test.design.component.preview.AutomotivePreviews
import com.test.design.component.theme.NissanSpacing
import com.test.design.component.theme.NissanTheme

@Composable
fun CustomTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    tabs: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(NissanSpacing.topBarHeight)
            .padding(horizontal = NissanSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBack && onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.height(NissanSpacing.minTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        tabs?.invoke()
    }
}

@AutomotivePreviews
@Composable
private fun CustomTopBarPreview() {
    NissanTheme {
        CustomTopBar(title = "Feature Playground")
    }
}

@AutomotivePreviews
@Composable
private fun CustomTopBarWithBackPreview() {
    NissanTheme {
        CustomTopBar(
            title = "Demo Screen",
            showBack = true,
            onBackClick = {},
        )
    }
}
