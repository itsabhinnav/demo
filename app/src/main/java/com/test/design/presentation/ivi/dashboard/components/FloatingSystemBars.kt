package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.design.presentation.ivi.climate.ClimateEvent
import com.test.design.presentation.ivi.climate.ClimateUiState
import com.test.design.presentation.ivi.climate.ClimateZone
import com.test.design.presentation.ivi.climate.formatTemperatureValue
import com.test.design.presentation.ivi.dashboard.FloatingSystemBarsVisibility
import com.test.design.theme.CarDesignTokens
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val FloatingSystemBarInset = 12.dp
/** Flush top/bottom to the app panel edges — AAOS already reserves system-bar bands outside. */
val FloatingSystemBarEdgeInset = 0.dp
val FloatingTopBarHeight = 64.dp
val FloatingBottomBarHeight = 84.dp

/** Space reserved under the floating top bar (edge inset + bar height). */
val FloatingChromeTopSpace = FloatingSystemBarEdgeInset + FloatingTopBarHeight

/** Space reserved above the floating bottom bar (edge inset + bar height). */
val FloatingChromeBottomSpace = FloatingSystemBarEdgeInset + FloatingBottomBarHeight

/** Current top chrome inset — collapses to 0 when bars are hidden via adb. */
@Composable
fun rememberedFloatingChromeTopSpace(): Dp {
    val visible by FloatingSystemBarsVisibility.visible.collectAsStateWithLifecycle()
    return if (visible) FloatingChromeTopSpace else 0.dp
}

/** Current bottom chrome inset — collapses to 0 when bars are hidden via adb. */
@Composable
fun rememberedFloatingChromeBottomSpace(): Dp {
    val visible by FloatingSystemBarsVisibility.visible.collectAsStateWithLifecycle()
    return if (visible) FloatingChromeBottomSpace else 0.dp
}

@Composable
fun Modifier.floatingSystemChromePadding(): Modifier {
    val top = rememberedFloatingChromeTopSpace()
    val bottom = rememberedFloatingChromeBottomSpace()
    return padding(top = top, bottom = bottom)
}

private val FloatingBarShape = RoundedCornerShape(28.dp)
private val GlassBarColor = Color(0xF01C1C1E)
private val IconMuted = Color.White.copy(alpha = 0.78f)
private val FloatingSectionPaddingH = 18.dp
private val FloatingSectionPaddingV = 10.dp

@Composable
private fun FloatingSection(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = FloatingBarShape,
        color = GlassBarColor,
        shadowElevation = 8.dp,
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = FloatingSectionPaddingH,
                vertical = FloatingSectionPaddingV,
            ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
fun FloatingTopSystemBar(
    modifier: Modifier = Modifier,
    driverLabel: String = "Driver",
    onNotificationsClick: () -> Unit = {},
    notificationCount: Int = 0,
) {
    val time = rememberClockText()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FloatingTopBarHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingSection {
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusIcon(Icons.Default.Bluetooth, "Bluetooth")
                StatusIcon(Icons.Default.Wifi, "Wi‑Fi")
                StatusIcon(Icons.Default.WbSunny, "Brightness")
                StatusIcon(Icons.AutoMirrored.Filled.VolumeUp, "Volume")
            }
        }

        FloatingSection {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = time,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Location",
                    tint = IconMuted,
                    modifier = Modifier.size(28.dp),
                )
            }
        }

        FloatingSection {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NotificationStatusButton(
                    count = notificationCount,
                    onClick = onNotificationsClick,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.14f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Text(
                        text = driverLabel,
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingBottomSystemBar(
    climateState: ClimateUiState,
    onClimateEvent: (ClimateEvent) -> Unit,
    onOpenApps: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenAssistant: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val driverTemp = climateState.formatTemperatureValue(climateState.temperatureCelsius)
    val passengerTemp = climateState.formatTemperatureValue(
        climateState.passengerTemperatureCelsius,
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(FloatingBottomBarHeight),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingSection {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BarIconButton(
                    icon = Icons.Default.GridView,
                    contentDescription = "All apps",
                    onClick = onOpenApps,
                )
                if (climateState.capabilities.hasDriverTemp) {
                    ZoneTemperatureControl(
                        temperature = driverTemp,
                        onDecrease = {
                            onClimateEvent(
                                ClimateEvent.AdjustZoneTemperature(ClimateZone.Driver, -1),
                            )
                        },
                        onIncrease = {
                            onClimateEvent(
                                ClimateEvent.AdjustZoneTemperature(ClimateZone.Driver, +1),
                            )
                        },
                        contentDescription = "Driver temperature",
                    )
                }
            }
        }

        FloatingSection {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DockIcon(
                    icon = Icons.Default.Apps,
                    contentDescription = "App drawer",
                    container = Color(0xFF3A3A3C),
                    onClick = onOpenApps,
                )
                DockIcon(
                    icon = Icons.Default.Settings,
                    contentDescription = "Settings",
                    container = Color(0xFF2F6FED),
                    onClick = onOpenSettings,
                )
                DockIcon(
                    icon = Icons.Default.Home,
                    contentDescription = "Home",
                    container = Color(0xFF48484A),
                    tint = Color.White,
                    onClick = onOpenHome,
                )
                DockIcon(
                    icon = Icons.Default.AutoAwesome,
                    contentDescription = "Assistant",
                    container = Color(0xFF5B4CDB),
                    onClick = onOpenAssistant,
                )
                DockIcon(
                    icon = Icons.Default.Phone,
                    contentDescription = "Phone",
                    container = Color(0xFF1B7F4A),
                    onClick = onOpenApps,
                )
                DockIcon(
                    icon = Icons.Default.Android,
                    contentDescription = "Android",
                    container = Color(0xFF3DDC84),
                    tint = Color(0xFF073042),
                    onClick = onOpenHome,
                )
            }
        }

        FloatingSection {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (climateState.capabilities.hasPassengerTemp) {
                    ZoneTemperatureControl(
                        temperature = passengerTemp,
                        onDecrease = {
                            onClimateEvent(
                                ClimateEvent.AdjustZoneTemperature(ClimateZone.Passenger, -1),
                            )
                        },
                        onIncrease = {
                            onClimateEvent(
                                ClimateEvent.AdjustZoneTemperature(ClimateZone.Passenger, +1),
                            )
                        },
                        contentDescription = "Passenger temperature",
                    )
                }
                BarIconButton(
                    icon = Icons.Default.Mic,
                    contentDescription = "Voice assistant",
                    onClick = onOpenAssistant,
                )
            }
        }
    }
}

@Composable
private fun StatusIcon(
    icon: ImageVector,
    contentDescription: String,
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = IconMuted,
        modifier = Modifier.size(28.dp),
    )
}

@Composable
private fun NotificationStatusButton(
    count: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .semantics {
                role = Role.Button
                contentDescription = if (count > 0) {
                    "Notifications, $count unread"
                } else {
                    "Notifications"
                }
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = IconMuted,
            modifier = Modifier.size(28.dp),
        )
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(16.dp)
                    .background(Color(0xFFE53935), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = count.coerceAtMost(9).toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun BarIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp = CarDesignTokens.MinTouchTarget,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = IconMuted,
            modifier = Modifier.size(CarDesignTokens.SecondaryIcon),
        )
    }
}

@Composable
private fun ZoneTemperatureControl(
    temperature: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    contentDescription: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.semantics {
            this.contentDescription = "$contentDescription $temperature"
        },
    ) {
        TempStepButton(Icons.Default.Remove, "Decrease $contentDescription", onDecrease)
        Text(
            text = temperature,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(56.dp),
            softWrap = false,
            maxLines = 1,
        )
        TempStepButton(Icons.Default.Add, "Increase $contentDescription", onIncrease)
    }
}

@Composable
private fun TempStepButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun DockIcon(
    icon: ImageVector,
    contentDescription: String,
    container: Color,
    onClick: () -> Unit,
    tint: Color = Color.White,
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .semantics {
                role = Role.Button
                this.contentDescription = contentDescription
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun rememberClockText(): String {
    var text by remember {
        mutableStateOf(formatClock(System.currentTimeMillis()))
    }
    LaunchedEffect(Unit) {
        while (true) {
            text = formatClock(System.currentTimeMillis())
            delay(15_000)
        }
    }
    return text
}

private fun formatClock(millis: Long): String =
    SimpleDateFormat("h:mm", Locale.getDefault()).format(Date(millis))
