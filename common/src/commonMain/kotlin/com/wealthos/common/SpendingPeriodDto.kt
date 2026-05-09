package com.wealthos.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SpendingPeriodDto(
    val id: String?,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: Instant,
    val entries: List<SpendingEntry>,
    
    val totalIncome: Double,
    val totalNeeds: Double,
    val totalWants: Double,
    val totalSavings: Double,
    val totalSpending: Double,
    val balance: Double,
    val needsPercentage: Double,
    val wantsPercentage: Double,
    val savingsPercentage: Double,
    val status: String
)

fun SpendingPeriod.toDto(): SpendingPeriodDto {
    val balanceValue = balance
    val statusValue = when {
        balanceValue < 0 -> "CRITICAL"
        balanceValue < 500 -> "WARNING"
        else -> "HEALTHY"
    }
    
    return SpendingPeriodDto(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        entries = entries,
        totalIncome = totalIncome,
        totalNeeds = totalNeeds,
        totalWants = totalWants,
        totalSavings = totalSavings,
        totalSpending = totalSpending,
        balance = balanceValue,
        needsPercentage = needsPercentage,
        wantsPercentage = wantsPercentage,
        savingsPercentage = savingsPercentage,
        status = statusValue
    )
}
