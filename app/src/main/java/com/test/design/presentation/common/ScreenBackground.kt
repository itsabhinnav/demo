package com.test.design.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.test.design.R

@Composable
fun ScreenBackground(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.background_image_lhd),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun ScreenWithBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        ScreenBackground()
        content()
    }
}
