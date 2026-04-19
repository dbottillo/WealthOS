package com.wealthos.common

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class SpendingPeriodTest {

    @Test
    fun testCalculations() {
        val period = SpendingPeriod(
            name = "Test Period",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 31),
            createdAt = Clock.System.now(),
            salary = 5000.0,
            otherIncome = 200.0,
            partnerContributions = 300.0,
            mortgage = 1500.0,
            bills = 500.0,
            groceries = 400.0,
            transport = 100.0,
            personalCare = 50.0,
            dentist = 50.0,
            expenses = 150.0,
            eatingOut = 300.0,
            shopping = 400.0,
            entertainment = 100.0,
            savings = 1000.0,
            investment = 500.0,
            sipp = 500.0
        )

        assertEquals(5500.0, period.totalIncome)
        assertEquals(2750.0, period.totalNeeds)
        assertEquals(800.0, period.totalWants)
        assertEquals(2000.0, period.totalSavings)
        assertEquals(5550.0, period.totalSpending)
        assertEquals(-50.0, period.balance)
        
        assertEquals(0.5, period.needsPercentage) // 2750 / 5500
        assertEquals(800.0 / 5500.0, period.wantsPercentage)
        assertEquals(2000.0 / 5500.0, period.savingsPercentage)
    }

    @Test
    fun testZeroIncome() {
        val period = SpendingPeriod(
            name = "Zero Income",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 31),
            createdAt = Clock.System.now(),
            salary = 0.0
        )
        
        assertEquals(0.0, period.totalIncome)
        assertEquals(0.0, period.needsPercentage)
        assertEquals(0.0, period.wantsPercentage)
        assertEquals(0.0, period.savingsPercentage)
    }
}
