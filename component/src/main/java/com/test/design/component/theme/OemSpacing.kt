package com.test.design.component.theme

import androidx.compose.ui.unit.dp

object OemSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp

    /** AAOS minimum — Google Design for Driving recommends 76dp, not phone 48dp. */
    val minTouchTarget = 76.dp
    val drivingTouchTarget = 84.dp
    val restrictedTouchTarget = 88.dp

    val listItemHeight = 80.dp
    val drivingListItemHeight = 84.dp
    val restrictedListItemHeight = 88.dp

    val topBarHeight = 88.dp
}
