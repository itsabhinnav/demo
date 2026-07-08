package com.test.design.presentation.ivi.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.test.design.theme.AppTheme

@Preview(widthDp = 1920, heightDp = 720)
@Composable
private fun IviDemoScreenPreview() {
    AppTheme {
        IviDemoScreen(onExit = {})
    }
}
