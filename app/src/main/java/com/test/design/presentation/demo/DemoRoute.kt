package com.test.design.presentation.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.presentation.demos.DemoContentRouter

@Composable
fun DemoRoute(
    demoId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoContentRouter(
        demoId = demoId,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}
