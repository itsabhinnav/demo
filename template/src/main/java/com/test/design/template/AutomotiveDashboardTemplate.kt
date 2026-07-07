package com.test.design.template

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomTopBar
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemTheme
import com.test.design.template.adaptive.AutomotiveWindowInfo
import com.test.design.template.adaptive.rememberAutomotiveWindowInfo
import com.test.design.template.preview.AutomotivePreviews
import com.test.design.template.zones.BlueZone
import com.test.design.template.zones.GreenZone
import com.test.design.template.zones.YellowZone

val LocalAutomotiveWindowInfo = compositionLocalOf<AutomotiveWindowInfo> {
    error("AutomotiveWindowInfo not provided")
}

@Composable
fun AutomotiveDashboardTemplate(
    blueZone: @Composable () -> Unit,
    greenZone: @Composable () -> Unit,
    yellowZone: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    showBlueZone: Boolean = true,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val windowInfo = rememberAutomotiveWindowInfo(maxWidth, maxHeight)
        val blueZoneHeight = (maxHeight * windowInfo.blueZoneHeightFraction).coerceAtLeast(96.dp)

        CompositionLocalProvider(LocalAutomotiveWindowInfo provides windowInfo) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (showBlueZone) {
                    BlueZone(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(blueZoneHeight),
                    ) {
                        blueZone()
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    GreenZone(
                        modifier = Modifier
                            .weight(windowInfo.leftColumnWeight)
                            .fillMaxHeight(),
                    ) {
                        greenZone()
                    }
                    YellowZone(
                        modifier = Modifier
                            .weight(windowInfo.rightColumnWeight)
                            .fillMaxHeight(),
                    ) {
                        yellowZone()
                    }
                }
            }
        }
    }
}

@AutomotivePreviews
@Composable
private fun AutomotiveDashboardTemplatePreview() {
    OemTheme {
        AutomotiveDashboardTemplate(
            blueZone = { CustomTopBar(title = "Feature Playground") },
            greenZone = {
                Text(
                    text = "Green Zone — main content area",
                    modifier = Modifier.padding(OemSpacing.md),
                )
            },
            yellowZone = {
                Text(
                    text = "Yellow Zone — supplementary controls",
                    modifier = Modifier.padding(OemSpacing.md),
                )
            },
        )
    }
}
