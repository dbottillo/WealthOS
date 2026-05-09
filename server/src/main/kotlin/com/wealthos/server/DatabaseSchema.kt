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

    override val primaryKey = PrimaryKey(id)
}

object Categories : Table("categories") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex()
    val bucket = varchar("bucket", 50) // INCOME, NEED, WANT, SAVING, UNCATEGORIZED

    override val primaryKey = PrimaryKey(id)
}

object PeriodEntries : Table("period_entries") {
    val id = integer("id").autoIncrement()
    val periodId = integer("period_id").references(SpendingPeriods.id)
    val categoryId = integer("category_id").references(Categories.id)
    val amount = double("amount").default(0.0)

    override val primaryKey = PrimaryKey(id)
    init {
        uniqueIndex(periodId, categoryId)
    }
}
