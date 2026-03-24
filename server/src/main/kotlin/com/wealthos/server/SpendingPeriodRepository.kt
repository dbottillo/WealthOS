package com.wealthos.server

import com.wealthos.common.SpendingPeriod
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object SpendingPeriodRepository {

    private fun ResultRow.toSpendingPeriod() = SpendingPeriod(
        name = this[SpendingPeriods.name],
        startDate = this[SpendingPeriods.startDate],
        endDate = this[SpendingPeriods.endDate],
        createdAt = this[SpendingPeriods.createdAt],
        salary = this[SpendingPeriods.salary],
        otherIncome = this[SpendingPeriods.otherIncome],
        partnerContributions = this[SpendingPeriods.partnerContributions],
        mortgage = this[SpendingPeriods.mortgage],
        bills = this[SpendingPeriods.bills],
        groceries = this[SpendingPeriods.groceries],
        transport = this[SpendingPeriods.transport],
        personalCare = this[SpendingPeriods.personalCare],
        dentist = this[SpendingPeriods.dentist],
        expenses = this[SpendingPeriods.expenses],
        eatingOut = this[SpendingPeriods.eatingOut],
        shopping = this[SpendingPeriods.shopping],
        entertainment = this[SpendingPeriods.entertainment],
        books = this[SpendingPeriods.books],
        clothing = this[SpendingPeriods.clothing],
        gifts = this[SpendingPeriods.gifts],
        tech = this[SpendingPeriods.tech],
        drinks = this[SpendingPeriods.drinks],
        holidays = this[SpendingPeriods.holidays],
        lego = this[SpendingPeriods.lego],
        gaming = this[SpendingPeriods.gaming],
        comics = this[SpendingPeriods.comics],
        psychotherapy = this[SpendingPeriods.psychotherapy],
        gym = this[SpendingPeriods.gym],
        cycling = this[SpendingPeriods.cycling],
        culture = this[SpendingPeriods.culture],
        parents = this[SpendingPeriods.parents],
        savings = this[SpendingPeriods.savings],
        investment = this[SpendingPeriods.investment],
        sipp = this[SpendingPeriods.sipp]
    )

    fun findAll(): List<SpendingPeriod> = transaction {
        SpendingPeriods.selectAll().map { it.toSpendingPeriod() }
    }

    fun findById(id: Int): SpendingPeriod? = transaction {
        SpendingPeriods.selectAll().where { SpendingPeriods.id eq id }
            .map { it.toSpendingPeriod() }
            .singleOrNull()
    }

    fun add(period: SpendingPeriod): Int = transaction {
        SpendingPeriods.insert {
            it[name] = period.name
            it[startDate] = period.startDate
            it[endDate] = period.endDate
            it[createdAt] = period.createdAt
            it[salary] = period.salary
            it[otherIncome] = period.otherIncome
            it[partnerContributions] = period.partnerContributions
            it[mortgage] = period.mortgage
            it[bills] = period.bills
            it[groceries] = period.groceries
            it[transport] = period.transport
            it[personalCare] = period.personalCare
            it[dentist] = period.dentist
            it[expenses] = period.expenses
            it[eatingOut] = period.eatingOut
            it[shopping] = period.shopping
            it[entertainment] = period.entertainment
            it[books] = period.books
            it[clothing] = period.clothing
            it[gifts] = period.gifts
            it[tech] = period.tech
            it[drinks] = period.drinks
            it[holidays] = period.holidays
            it[lego] = period.lego
            it[gaming] = period.gaming
            it[comics] = period.comics
            it[psychotherapy] = period.psychotherapy
            it[gym] = period.gym
            it[cycling] = period.cycling
            it[culture] = period.culture
            it[parents] = period.parents
            it[savings] = period.savings
            it[investment] = period.investment
            it[sipp] = period.sipp
        }[SpendingPeriods.id]
    }

    fun update(id: Int, period: SpendingPeriod): Boolean = transaction {
        SpendingPeriods.update({ SpendingPeriods.id eq id }) {
            it[name] = period.name
            it[startDate] = period.startDate
            it[endDate] = period.endDate
            it[salary] = period.salary
            it[otherIncome] = period.otherIncome
            it[partnerContributions] = period.partnerContributions
            it[mortgage] = period.mortgage
            it[bills] = period.bills
            it[groceries] = period.groceries
            it[transport] = period.transport
            it[personalCare] = period.personalCare
            it[dentist] = period.dentist
            it[expenses] = period.expenses
            it[eatingOut] = period.eatingOut
            it[shopping] = period.shopping
            it[entertainment] = period.entertainment
            it[books] = period.books
            it[clothing] = period.clothing
            it[gifts] = period.gifts
            it[tech] = period.tech
            it[drinks] = period.drinks
            it[holidays] = period.holidays
            it[lego] = period.lego
            it[gaming] = period.gaming
            it[comics] = period.comics
            it[psychotherapy] = period.psychotherapy
            it[gym] = period.gym
            it[cycling] = period.cycling
            it[culture] = period.culture
            it[parents] = period.parents
            it[savings] = period.savings
            it[investment] = period.investment
            it[sipp] = period.sipp
        } > 0
    }

    fun delete(id: Int): Boolean = transaction {
        SpendingPeriods.deleteWhere { SpendingPeriods.id eq id } > 0
    }
}
