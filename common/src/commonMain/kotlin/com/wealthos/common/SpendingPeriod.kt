package com.wealthos.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SpendingPeriod(
    val id: String? = null,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: Instant,
    
    // Incomes
    val salary: Double = 0.0,
    val otherIncome: Double = 0.0,
    val partnerContributions: Double = 0.0,
    
    // Needs
    val mortgage: Double = 0.0,
    val bills: Double = 0.0,
    val groceries: Double = 0.0,
    val transport: Double = 0.0,
    val personalCare: Double = 0.0,
    val dentist: Double = 0.0,
    val expenses: Double = 0.0,
    
    // Wants
    val eatingOut: Double = 0.0,
    val shopping: Double = 0.0,
    val entertainment: Double = 0.0,
    val books: Double = 0.0,
    val clothing: Double = 0.0,
    val gifts: Double = 0.0,
    val tech: Double = 0.0,
    val drinks: Double = 0.0,
    val holidays: Double = 0.0,
    val lego: Double = 0.0,
    val gaming: Double = 0.0,
    val comics: Double = 0.0,
    val psychotherapy: Double = 0.0,
    val gym: Double = 0.0,
    val cycling: Double = 0.0,
    val culture: Double = 0.0,
    val parents: Double = 0.0,
    
    // Savings
    val savings: Double = 0.0,
    val investment: Double = 0.0,
    val sipp: Double = 0.0
) {
    val totalIncome: Double get() = salary + otherIncome + partnerContributions
    
    val totalNeeds: Double get() = mortgage + bills + groceries + transport + personalCare + dentist + expenses
    
    val totalWants: Double get() = eatingOut + shopping + entertainment + books + clothing + gifts + 
        tech + drinks + holidays + lego + gaming + comics + psychotherapy + gym + cycling + culture + parents
        
    val totalSavings: Double get() = savings + investment + sipp
    
    val totalSpending: Double get() = totalNeeds + totalWants + totalSavings
    
    val balance: Double get() = totalIncome - totalSpending
    
    val needsPercentage: Double get() = if (totalIncome > 0) totalNeeds / totalIncome else 0.0
    val wantsPercentage: Double get() = if (totalIncome > 0) totalWants / totalIncome else 0.0
    val savingsPercentage: Double get() = if (totalIncome > 0) totalSavings / totalIncome else 0.0
}
