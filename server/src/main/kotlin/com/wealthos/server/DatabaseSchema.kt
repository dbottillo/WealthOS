package com.wealthos.server

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SpendingPeriods : Table("spending_periods") {
    val id = integer("id").autoIncrement()
    val externalId = varchar("external_id", 255).nullable().uniqueIndex()
    val name = varchar("name", 255)
    val startDate = date("start_date")
    val endDate = date("end_date")
    val createdAt = timestamp("created_at")

    // Incomes
    val salary = double("salary").default(0.0)
    val otherIncome = double("other_income").default(0.0)
    val partnerContributions = double("partner_contributions").default(0.0)

    // Needs
    val mortgage = double("mortgage").default(0.0)
    val bills = double("bills").default(0.0)
    val groceries = double("groceries").default(0.0)
    val transport = double("transport").default(0.0)
    val personalCare = double("personal_care").default(0.0)
    val dentist = double("dentist").default(0.0)
    val expenses = double("expenses").default(0.0)

    // Wants
    val eatingOut = double("eating_out").default(0.0)
    val shopping = double("shopping").default(0.0)
    val entertainment = double("entertainment").default(0.0)
    val books = double("books").default(0.0)
    val clothing = double("clothing").default(0.0)
    val gifts = double("gifts").default(0.0)
    val tech = double("tech").default(0.0)
    val drinks = double("drinks").default(0.0)
    val holidays = double("holidays").default(0.0)
    val lego = double("lego").default(0.0)
    val gaming = double("gaming").default(0.0)
    val comics = double("comics").default(0.0)
    val psychotherapy = double("psychotherapy").default(0.0)
    val gym = double("gym").default(0.0)
    val cycling = double("cycling").default(0.0)
    val culture = double("culture").default(0.0)
    val parents = double("parents").default(0.0)

    // Savings
    val savings = double("savings").default(0.0)
    val investment = double("investment").default(0.0)
    val sipp = double("sipp").default(0.0)

    override val primaryKey = PrimaryKey(id)
}
