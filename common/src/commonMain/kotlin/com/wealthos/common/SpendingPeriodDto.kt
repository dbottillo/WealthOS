package com.wealthos.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SpendingPeriodDto(
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: Instant,
    
    // Incomes
    val salary: Double,
    val otherIncome: Double,
    val partnerContributions: Double,
    val totalIncome: Double,
    
    // Needs
    val mortgage: Double,
    val bills: Double,
    val groceries: Double,
    val transport: Double,
    val personalCare: Double,
    val dentist: Double,
    val expenses: Double,
    val totalNeeds: Double,
    
    // Wants
    val eatingOut: Double,
    val shopping: Double,
    val entertainment: Double,
    val books: Double,
    val clothing: Double,
    val gifts: Double,
    val tech: Double,
    val drinks: Double,
    val holidays: Double,
    val lego: Double,
    val gaming: Double,
    val comics: Double,
    val psychotherapy: Double,
    val gym: Double,
    val cycling: Double,
    val culture: Double,
    val parents: Double,
    val totalWants: Double,
    
    // Savings
    val savings: Double,
    val investment: Double,
    val sipp: Double,
    val totalSavings: Double,

    // Totals & Percentages
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
        name = name,
        startDate = startDate,
        endDate = endDate,
        createdAt = createdAt,
        salary = salary,
        otherIncome = otherIncome,
        partnerContributions = partnerContributions,
        totalIncome = totalIncome,
        mortgage = mortgage,
        bills = bills,
        groceries = groceries,
        transport = transport,
        personalCare = personalCare,
        dentist = dentist,
        expenses = expenses,
        totalNeeds = totalNeeds,
        eatingOut = eatingOut,
        shopping = shopping,
        entertainment = entertainment,
        books = books,
        clothing = clothing,
        gifts = gifts,
        tech = tech,
        drinks = drinks,
        holidays = holidays,
        lego = lego,
        gaming = gaming,
        comics = comics,
        psychotherapy = psychotherapy,
        gym = gym,
        cycling = cycling,
        culture = culture,
        parents = parents,
        totalWants = totalWants,
        savings = savings,
        investment = investment,
        sipp = sipp,
        totalSavings = totalSavings,
        totalSpending = totalSpending,
        balance = balanceValue,
        needsPercentage = needsPercentage,
        wantsPercentage = wantsPercentage,
        savingsPercentage = savingsPercentage,
        status = statusValue
    )
}
