package com.test.design.presentation.demos.shared

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomTopBar
import com.test.design.component.theme.NissanSpacing
import com.test.design.template.AutomotiveDashboardTemplate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

@Composable
fun DemoScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    yellowContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AutomotiveDashboardTemplate(
        modifier = modifier,
        blueZone = {
            CustomTopBar(
                title = title,
                showBack = true,
                onBackClick = onBack,
            )
        },
        greenZone = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = NissanSpacing.lg),
            ) {
                content()
            }
        },
        yellowZone = yellowContent,
    )
}
