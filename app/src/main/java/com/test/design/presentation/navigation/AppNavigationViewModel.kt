package com.test.design.presentation.navigation

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.test.design.core.navigation.DeepLinkParser
import com.test.design.domain.repository.FeatureDemoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PendingNavigation(
    val demoId: String,
    val isValid: Boolean,
)

class AppNavigationViewModel(
    private val repository: FeatureDemoRepository,
) : ViewModel() {

    private val _pendingNavigation = MutableStateFlow<PendingNavigation?>(null)
    val pendingNavigation: StateFlow<PendingNavigation?> = _pendingNavigation.asStateFlow()

    fun handleIntent(intent: Intent?) {
        val demoId = DeepLinkParser.parseDemoId(intent) ?: return
        _pendingNavigation.value = PendingNavigation(
            demoId = demoId,
            isValid = repository.findById(demoId) != null,
        )
    }

    fun consumePendingNavigation() {
        _pendingNavigation.update { null }
    }
}
