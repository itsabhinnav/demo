package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.TurnSlightRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.navigation.NavigationUiState

/**
 * Compact turn-by-turn trip card — explicit sp sizes (CarTypography is too large).
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.TripGuidanceCard(
    navigationState: NavigationUiState,
    onExpand: () -> Unit,
    onCancelTrip: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()

    Surface(
        onClick = onExpand,
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Navigation,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier.widthIn(max = 300.dp),
        ),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xE6111114),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.TurnSlightRight,
                    contentDescription = null,
                    tint = Color(0xFF4EA1FF),
                    modifier = Modifier.size(28.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = navigationState.routeSteps.firstOrNull()?.distanceLabel
                            ?: navigationState.distanceRemaining,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp,
                    )
                    AnimatedContent(
                        targetState = navigationState.currentInstruction,
                        transitionSpec = {
                            fadeIn(effectsSpec) togetherWith fadeOut(effectsSpec)
                        },
                        label = "trip_instruction",
                    ) { instruction ->
                        Text(
                            text = instruction,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            navigationState.routeSteps.getOrNull(1)?.let { next ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "${next.distanceLabel}  ${next.instruction}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Button(
                onClick = onCancelTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.12f),
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = "CANCEL TRIP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TripStat(navigationState.distanceRemaining)
                TripStat("${navigationState.etaMinutes} min")
                TripStat(navigationState.arrivalTime)
            }
        }
    }
}

@Composable
private fun TripStat(label: String) {
    Text(
        text = label,
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    )
}
