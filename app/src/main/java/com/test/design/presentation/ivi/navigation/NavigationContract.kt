package com.test.design.presentation.ivi.navigation

data class FavoritePlace(
    val id: String,
    val name: String,
    val etaLabel: String,
)

data class RouteStep(
    val id: String,
    val instruction: String,
    val distanceLabel: String,
)

data class NavigationUiState(
    val destination: String = "Home",
    val etaMinutes: Int = 12,
    val distanceRemaining: String = "4.2 mi",
    val arrivalTime: String = "5:42 PM",
    val currentInstruction: String = "Turn right onto Market St",
    val maneuverIcon: String = "→",
    val favorites: List<FavoritePlace> = listOf(
        FavoritePlace("home", "Home", "12 min"),
        FavoritePlace("work", "Work", "24 min"),
        FavoritePlace("charge", "Charge Hub", "8 min"),
    ),
    val routeSteps: List<RouteStep> = listOf(
        RouteStep("1", "Turn right onto Market St", "0.3 mi"),
        RouteStep("2", "Merge onto Highway 101 North", "2.1 mi"),
        RouteStep("3", "Take exit 14 toward Downtown", "1.2 mi"),
        RouteStep("4", "Arrive at Home", "0.6 mi"),
    ),
    val selectedFavoriteId: String? = "home",
    val showRouteDetails: Boolean = false,
)

sealed interface NavigationEvent {
    data class SelectFavorite(val id: String) : NavigationEvent
    data object ToggleRouteDetails : NavigationEvent
    data object NextManeuver : NavigationEvent
}
