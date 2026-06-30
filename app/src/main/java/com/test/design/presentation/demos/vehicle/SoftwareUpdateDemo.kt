package com.test.design.presentation.demos.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomDialog
import com.test.design.component.components.CustomLinearProgress
import com.test.design.component.components.CustomListTile
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSnackbarMessage
import com.test.design.component.components.CustomStatusIndicator
import com.test.design.component.components.StatusLevel
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun SoftwareUpdateDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var downloadProgress by remember { mutableFloatStateOf(0.65f) }
    var showInstallDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    if (showInstallDialog) {
        CustomDialog(
            title = "Install update?",
            message = "Vehicle must be parked with at least 20% battery. Installation takes about 25 minutes.",
            confirmText = "Install now",
            dismissText = "Cancel",
            onConfirm = {
                showInstallDialog = false
                snackbarMessage = "Update scheduled — install begins when parked"
            },
            onDismiss = { showInstallDialog = false },
        )
    }

    DemoScaffold(
        title = "Software Update",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            Column {
                DemoTipsPanel(
                    tips = listOf(
                        "OTA updates require parked + sufficient battery",
                        "Show clear progress during download and install",
                        "Confirm destructive actions with a dialog",
                    ),
                )
                CustomStatusIndicator(
                    label = "Vehicle parked — ready to install",
                    level = StatusLevel.Normal,
                    modifier = Modifier.padding(top = OemSpacing.md),
                )
            }
        },
    ) {
        UpdateStatusSection()
        DownloadProgressSection(progress = downloadProgress)
        ReleaseNotesSection()
        ActionSection(
            onInstallClick = { showInstallDialog = true },
            onScheduleClick = { snackbarMessage = "Update scheduled for tonight at 2:00 AM" },
        )
        snackbarMessage?.let { message ->
            CustomSnackbarMessage(
                message = message,
                modifier = Modifier.padding(top = OemSpacing.md),
            )
        }
    }
}

@Composable
private fun UpdateStatusSection() {
    CustomSectionHeader(
        title = "Available Update",
        subtitle = "Version 2026.06.1 — 1.2 GB",
    )
    CustomStatusIndicator(
        label = "Update ready — v2026.06.1",
        level = StatusLevel.Info,
        modifier = Modifier.padding(vertical = OemSpacing.md),
    )
}

@Composable
private fun DownloadProgressSection(progress: Float) {
    CustomSectionHeader(
        title = "Download",
        subtitle = "Package download progress",
    )
    CustomLinearProgress(
        progress = { progress },
        label = "Downloaded — ${(progress * 100).toInt()}%",
        modifier = Modifier.padding(vertical = OemSpacing.md),
    )
}

@Composable
private fun ReleaseNotesSection() {
    CustomSectionHeader(
        title = "Release Notes",
        subtitle = "What's included in this update",
    )
    CustomListTile(
        title = "Navigation improvements",
        subtitle = "Faster route recalculation and lane guidance",
        leadingIcon = Icons.Default.NewReleases,
        onClick = {},
    )
    CustomListTile(
        title = "Security patch",
        subtitle = "CVE-2026-1042 — telematics module hardening",
        leadingIcon = Icons.Default.Security,
        onClick = {},
    )
    CustomListTile(
        title = "Bug fixes",
        subtitle = "Climate sync and wireless CarPlay stability",
        leadingIcon = Icons.Default.BugReport,
        onClick = {},
    )
}

@Composable
private fun ActionSection(
    onInstallClick: () -> Unit,
    onScheduleClick: () -> Unit,
) {
    CustomSectionHeader(
        title = "Actions",
        subtitle = "Install or schedule the update",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomButton(
            text = "Install now",
            onClick = onInstallClick,
            style = ButtonStyle.Primary,
            modifier = Modifier.weight(1f),
        )
        CustomButton(
            text = "Schedule",
            onClick = onScheduleClick,
            style = ButtonStyle.Secondary,
            modifier = Modifier.weight(1f),
        )
    }
}
