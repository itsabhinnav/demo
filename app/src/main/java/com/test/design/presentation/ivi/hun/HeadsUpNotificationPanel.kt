package com.test.design.presentation.ivi.hun

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.SimulatedBadge
import com.test.design.theme.CarDesignTokens

enum class HunCategory {
    Navigation,
    Call,
    Climate,
}

data class HunNotification(
    val id: String,
    val category: HunCategory,
    val title: String,
    val body: String,
)

val DemoHunNotifications = listOf(
    HunNotification(
        id = "nav_turn",
        category = HunCategory.Navigation,
        title = "In 400 ft",
        body = "Keep right onto Horizon Blvd",
    ),
    HunNotification(
        id = "call_incoming",
        category = HunCategory.Call,
        title = "Incoming call",
        body = "Alex Chen · Mobile",
    ),
    HunNotification(
        id = "hvac_change",
        category = HunCategory.Climate,
        title = "Climate",
        body = "Driver zone set to 22° · Auto",
    ),
)

private fun HunCategory.icon(): ImageVector = when (this) {
    HunCategory.Navigation -> Icons.Default.Navigation
    HunCategory.Call -> Icons.Default.Phone
    HunCategory.Climate -> Icons.Default.AcUnit
}

private fun HunCategory.accent(): Color = when (this) {
    HunCategory.Navigation -> Color(0xFF5B9CFF)
    HunCategory.Call -> Color(0xFF34C759)
    HunCategory.Climate -> Color(0xFF7EC8E3)
}

@Composable
fun HeadsUpNotificationHost(
    visible: Boolean,
    notifications: List<HunNotification>,
    onDismiss: (String) -> Unit,
    onDismissAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible && notifications.isNotEmpty(),
        modifier = modifier,
        enter = slideInVertically { -it / 2 } + fadeIn(),
        exit = slideOutVertically { -it / 2 } + fadeOut(),
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF01C1C1E),
            shadowElevation = 12.dp,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Heads-up",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                        Text(
                            text = "Scalable UI HUN · OEM-brandable",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.6f),
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SimulatedBadge()
                        TextButton(onClick = onDismissAll) {
                            Text("Clear", color = Color(0xFF9EC5FF))
                        }
                    }
                }
                notifications.forEach { notification ->
                    HunCard(
                        notification = notification,
                        onDismiss = { onDismiss(notification.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HunCard(
    notification: HunNotification,
    onDismiss: () -> Unit,
) {
    val accent = notification.category.accent()
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.08f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDismiss),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = notification.category.icon(),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                )
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.72f),
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White.copy(alpha = 0.65f),
                )
            }
        }
    }
}
