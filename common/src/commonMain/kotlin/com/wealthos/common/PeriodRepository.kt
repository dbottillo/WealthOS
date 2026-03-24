package com.wealthos.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PeriodRepository(private val client: WealthOsClient) {
    private val _periods = MutableStateFlow<List<SpendingPeriodDto>>(emptyList())
    val periods: StateFlow<List<SpendingPeriodDto>> = _periods.asStateFlow()

    suspend fun refresh() {
        try {
            val fetchedPeriods = client.getPeriods()
            _periods.value = fetchedPeriods
        } catch (e: Exception) {
            // Log or handle error
        }
    }

    suspend fun addPeriod(period: SpendingPeriod): Int {
        val id = client.addPeriod(period)
        refresh()
        return id
    }

    suspend fun updatePeriod(id: Int, period: SpendingPeriod) {
        client.updatePeriod(id, period)
        refresh()
    }

    suspend fun deletePeriod(id: Int) {
        client.deletePeriod(id)
        refresh()
    }

    suspend fun triggerMigration() {
        client.triggerMigration()
        refresh()
    }
}
