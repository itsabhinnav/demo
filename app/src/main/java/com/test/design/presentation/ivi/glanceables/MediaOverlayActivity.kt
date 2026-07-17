package com.test.design.presentation.ivi.glanceables

import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.design.presentation.ivi.media.MediaViewModel

/**
 * Scalable UI `media_overlay` TaskPanel host — slides over the live map.
 */
class MediaOverlayActivity : GlanceableActivity() {

    private val mediaViewModel: MediaViewModel by viewModels()

    @Composable
    override fun GlanceContent() {
        val mediaState by mediaViewModel.state.collectAsStateWithLifecycle()
        GlanceRoot {
            MediaGlance(
                mediaState = mediaState,
                onMediaEvent = mediaViewModel::onEvent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            )
        }
    }
}
