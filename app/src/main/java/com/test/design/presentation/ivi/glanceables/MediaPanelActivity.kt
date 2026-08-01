package com.test.design.presentation.ivi.glanceables

import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.design.presentation.ivi.media.MediaPlayerScreen
import com.test.design.presentation.ivi.media.MediaViewModel

/** Standalone full media screen — launch via [ACTION_OPEN_MEDIA] or component name. */
class MediaPanelActivity : StandalonePanelActivity() {

    private val mediaViewModel: MediaViewModel by viewModels()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    override fun GlanceContent() {
        val mediaState by mediaViewModel.state.collectAsStateWithLifecycle()
        GlanceRoot {
            SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    MediaPlayerScreen(
                        uiState = mediaState,
                        onEvent = mediaViewModel::onEvent,
                        onBack = ::navigateHomeAndFinish,
                        animatedVisibilityScope = this,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
