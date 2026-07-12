package com.test.design.presentation.ivi.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.test.design.theme.AppTheme

@Preview(name = "Landscape AAOS", widthDp = 1920, heightDp = 720)
@Preview(name = "Portrait phone", widthDp = 411, heightDp = 891)
@Preview(name = "Tablet landscape", widthDp = 1280, heightDp = 800)
@Composable
private fun IviDemoScreenPreview() {
    AppTheme {
        IviDemoScreen()
    }
}
