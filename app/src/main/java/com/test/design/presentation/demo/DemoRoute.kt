package com.test.design.presentation.demo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.di.AppContainer
import com.test.design.presentation.demos.DemoContentRouter

@Composable
fun DemoRoute(
    demoId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRegistered = AppContainer.featureDemoRepository.findById(demoId) != null
    if (!isRegistered) {
        UnknownDemoScreen(
            demoId = demoId,
            onNavigateBack = onNavigateBack,
            modifier = modifier,
        )
        return
    }

    DemoContentRouter(
        demoId = demoId,
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    )
}
