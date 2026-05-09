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
    val status: String,

    // Legacy fields for backward compatibility/UI logic
    val salary: Double = 0.0,
    val otherIncome: Double = 0.0,
    val partnerContributions: Double = 0.0,
    val mortgage: Double = 0.0,
    val bills: Double = 0.0,
    val groceries: Double = 0.0,
    val transport: Double = 0.0,
    val personalCare: Double = 0.0,
    val dentist: Double = 0.0,
    val expenses: Double = 0.0,
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
    val savings: Double = 0.0,
    val investment: Double = 0.0,
    val sipp: Double = 0.0
)

fun SpendingPeriod.toDto(): SpendingPeriodDto {
    val balanceValue = balance
    val statusValue = when {
        balanceValue < 0 -> "CRITICAL"
        balanceValue < 500 -> "WARNING"
        else -> "HEALTHY"
    }

    fun getAmount(name: String): Double = entries.find { it.categoryName == name }?.amount ?: 0.0
    
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
        status = statusValue,
        
        salary = getAmount("Salary"),
        otherIncome = getAmount("Other income"),
        partnerContributions = getAmount("Fabio contributions"),
        mortgage = getAmount("Mortgage"),
        bills = getAmount("Bills"),
        groceries = getAmount("Groceries"),
        transport = getAmount("Transport"),
        personalCare = getAmount("Personal care"),
        dentist = getAmount("Dentist"),
        expenses = getAmount("Expenses"),
        eatingOut = getAmount("Eating out"),
        shopping = getAmount("Shopping"),
        entertainment = getAmount("Entertainment"),
        books = getAmount("Books"),
        clothing = getAmount("Clothing"),
        gifts = getAmount("Gifts"),
        tech = getAmount("Tech"),
        drinks = getAmount("Drinks"),
        holidays = getAmount("Holidays"),
        lego = getAmount("Lego"),
        gaming = getAmount("Gaming"),
        comics = getAmount("Comics"),
        psychotherapy = getAmount("Psycotherapy"),
        gym = getAmount("Gym"),
        cycling = getAmount("Cycling"),
        culture = getAmount("Culture"),
        parents = getAmount("Parents"),
        savings = getAmount("Savings"),
        investment = getAmount("Investment"),
        sipp = getAmount("SIPP")
    )
}
