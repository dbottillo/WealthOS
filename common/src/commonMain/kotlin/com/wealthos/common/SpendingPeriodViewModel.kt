package com.wealthos.common

import com.rickclephas.kmp.observableviewmodel.ViewModel
import com.rickclephas.kmp.observableviewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SpendingPeriodState(
    val periods: List<SpendingPeriodDto> = emptyList(),
    val healthStatus: String = "Unknown",
    val isLoading: Boolean = false,
    val error: String? = null
)

class SpendingPeriodViewModel(
    private val repository: PeriodRepository,
    private val client: WealthOsClient // keeping for health check for now
) : ViewModel() {
    
    private val _state = MutableStateFlow(SpendingPeriodState())
    val state: StateFlow<SpendingPeriodState> = _state.asStateFlow()

    init {
        viewModelScope.coroutineScope.launch {
            repository.periods.collect { periods ->
                _state.value = _state.value.copy(periods = periods)
            }
        }
        loadPeriods()
    }

    fun loadPeriods() {
        viewModelScope.coroutineScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.refresh()
                _state.value = _state.value.copy(isLoading = false, error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

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

    fun triggerMigration() {
        viewModelScope.coroutineScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                repository.triggerMigration()
                _state.value = _state.value.copy(isLoading = false, error = null)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
