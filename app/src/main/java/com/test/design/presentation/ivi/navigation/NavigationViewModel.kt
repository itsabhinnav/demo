package com.test.design.presentation.ivi.navigation

import com.test.design.core.mvi.MviViewModel

class NavigationViewModel : MviViewModel<NavigationUiState, NavigationEvent>(NavigationUiState()) {

    override fun onEvent(event: NavigationEvent) {
        when (event) {
            is NavigationEvent.SelectFavorite -> setState {
                val favorite = favorites.find { it.id == event.id } ?: return@setState this
                copy(
                    selectedFavoriteId = event.id,
                    destination = favorite.name,
                    etaMinutes = favorite.etaLabel.filter { it.isDigit() }.toIntOrNull() ?: etaMinutes,
                )
            }
            NavigationEvent.ToggleRouteDetails -> setState { copy(showRouteDetails = !showRouteDetails) }
            NavigationEvent.NextManeuver -> setState {
                val next = routeSteps.drop(1).firstOrNull() ?: routeSteps.first()
                copy(
                    currentInstruction = next.instruction,
                    maneuverIcon = if (maneuverIcon == "→") "↰" else "→",
                )
            }
        }
    }
}
