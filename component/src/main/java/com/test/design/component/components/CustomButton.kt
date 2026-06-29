package com.test.design.component.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.test.design.component.preview.AutomotivePreviews
import com.test.design.component.theme.NissanSpacing
import com.test.design.component.theme.NissanTheme

enum class ButtonStyle {
    Primary,
    Secondary,
    Tonal,
    Text,
    Destructive,
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val sizedModifier = modifier
        .defaultMinSize(minHeight = NissanSpacing.minTouchTarget)

    when (style) {
        ButtonStyle.Primary -> {
            Button(
                onClick = onClick,
                modifier = sizedModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }

        ButtonStyle.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = sizedModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }

        ButtonStyle.Tonal -> {
            FilledTonalButton(
                onClick = onClick,
                modifier = sizedModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }

        ButtonStyle.Text -> {
            TextButton(
                onClick = onClick,
                modifier = sizedModifier,
                enabled = enabled,
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }

        ButtonStyle.Destructive -> {
            Button(
                onClick = onClick,
                modifier = sizedModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@AutomotivePreviews
@Composable
private fun CustomButtonPreview() {
    NissanTheme {
        CustomButton(text = "Primary", onClick = {}, modifier = Modifier.padding(NissanSpacing.md))
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomButtonStylesPreview() {
    NissanTheme {
        CustomButton(text = "Secondary", onClick = {}, style = ButtonStyle.Secondary)
    }
}
