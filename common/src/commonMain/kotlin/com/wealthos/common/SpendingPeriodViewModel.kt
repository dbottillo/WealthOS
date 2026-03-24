package com.wealthos.common

import com.rickclephas.kmp.viewmodel.KMPViewModel
import com.rickclephas.kmp.viewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SpendingPeriodState(
    val healthStatus: String = "Checking...",
    val isLoading: Boolean = false
)

class SpendingPeriodViewModel(
    private val client: WealthOsClient
) : KMPViewModel() {
    
    private val _state = MutableStateFlow(SpendingPeriodState())
    val state: StateFlow<SpendingPeriodState> = _state.asStateFlow()

    fun checkHealth() {
        viewModelScope.coroutineScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val health = client.getHealth()
                _state.value = _state.value.copy(
                    healthStatus = health["status"] ?: "Unknown",
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    healthStatus = "Error: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
}
