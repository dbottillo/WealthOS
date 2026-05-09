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
            entries = listOf(
                SpendingEntry("Salary", 5000.0, "INCOME"),
                SpendingEntry("Other income", 200.0, "INCOME"),
                SpendingEntry("Fabio contributions", 300.0, "INCOME"),
                SpendingEntry("Mortgage", 1500.0, "NEED"),
                SpendingEntry("Bills", 500.0, "NEED"),
                SpendingEntry("Groceries", 400.0, "NEED"),
                SpendingEntry("Transport", 100.0, "NEED"),
                SpendingEntry("Personal care", 50.0, "NEED"),
                SpendingEntry("Dentist", 50.0, "NEED"),
                SpendingEntry("Expenses", 150.0, "NEED"),
                SpendingEntry("Eating out", 300.0, "WANT"),
                SpendingEntry("Shopping", 400.0, "WANT"),
                SpendingEntry("Entertainment", 100.0, "WANT"),
                SpendingEntry("Savings", 1000.0, "SAVING"),
                SpendingEntry("Investment", 500.0, "SAVING"),
                SpendingEntry("SIPP", 500.0, "SAVING")
            )
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
            entries = listOf(SpendingEntry("Salary", 0.0, "INCOME"))
        )
        
        assertEquals(0.0, period.totalIncome)
        assertEquals(0.0, period.needsPercentage)
        assertEquals(0.0, period.wantsPercentage)
        assertEquals(0.0, period.savingsPercentage)
    }
}
