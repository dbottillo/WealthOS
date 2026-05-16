package com.wealthos.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SpendingEntry(
    val categoryName: String,
    val amount: Double,
    val bucket: String = "UNCATEGORIZED"
)

@Serializable
data class SpendingPeriod(
    val id: String? = null,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: Instant,
    val entries: List<SpendingEntry> = emptyList()
) {
    val totalIncome: Double get() = entries.filter { it.bucket == "INCOME" }.sumOf { it.amount }
    
    val totalNeeds: Double get() = entries.filter { it.bucket == "NEED" }.sumOf { it.amount }
    
    val totalWants: Double get() = entries.filter { it.bucket == "WANT" }.sumOf { it.amount }
        
    val totalSavings: Double get() = entries.filter { it.bucket == "SAVING" }.sumOf { it.amount }
    
    val totalSpending: Double get() = totalNeeds + totalWants + totalSavings
    
    val balance: Double get() = totalIncome - totalSpending
    
    val needsPercentage: Double get() = if (totalSpending > 0) totalNeeds / totalSpending else 0.0
    val wantsPercentage: Double get() = if (totalSpending > 0) totalWants / totalSpending else 0.0
    val savingsPercentage: Double get() = if (totalSpending > 0) totalSavings / totalSpending else 0.0
}
