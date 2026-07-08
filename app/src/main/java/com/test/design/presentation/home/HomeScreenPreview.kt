package com.test.design.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.design.theme.AppTheme

@Preview(widthDp = 1920, heightDp = 720)
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
            onNavigateToIviDemo = {},
            onNavigateToMaterialComponents = {},
            onNavigateToMotionPhysicsSample = {},
        )
    }
}
