package com.test.design.component.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.theme.NissanSpacing

enum class CardStyle {
    Elevated,
    Filled,
    Outlined,
}

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    style: CardStyle = CardStyle.Filled,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = when (style) {
        CardStyle.Elevated -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        CardStyle.Filled -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
        CardStyle.Outlined -> CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    }

    val cardModifier = modifier.fillMaxWidth()

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = cardModifier,
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = NissanSpacing.xs),
            content = { Column(Modifier.padding(NissanSpacing.md), content = content) },
        )
    } else {
        Card(
            modifier = cardModifier,
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = NissanSpacing.xs),
            content = { Column(Modifier.padding(NissanSpacing.md), content = content) },
        )
    }
}
