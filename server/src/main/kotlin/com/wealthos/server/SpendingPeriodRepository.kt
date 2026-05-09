package com.wealthos.server

import com.wealthos.common.SpendingPeriod
import com.wealthos.common.SpendingEntry
import com.wealthos.common.CategoryDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SpendingPeriodRepository {

    private fun loadEntries(periodId: Int): List<SpendingEntry> {
        return (PeriodEntries innerJoin Categories)
            .selectAll().where { PeriodEntries.periodId eq periodId }
            .map {
                SpendingEntry(
                    categoryName = it[Categories.name],
                    amount = it[PeriodEntries.amount],
                    bucket = it[Categories.bucket]
                )
            }
    }

    private fun ResultRow.toSpendingPeriod(entries: List<SpendingEntry>) = SpendingPeriod(
        id = this[SpendingPeriods.externalId],
        name = this[SpendingPeriods.name],
        startDate = this[SpendingPeriods.startDate],
        endDate = this[SpendingPeriods.endDate],
        createdAt = this[SpendingPeriods.createdAt],
        entries = entries
    )

    fun findAll(): List<SpendingPeriod> = transaction {
        val periods = SpendingPeriods.selectAll()
            .orderBy(SpendingPeriods.startDate to SortOrder.DESC)
            .map { it }
        
        periods.map { periodRow ->
            val entries = loadEntries(periodRow[SpendingPeriods.id])
            periodRow.toSpendingPeriod(entries)
        }
    }

    fun findById(id: Int): SpendingPeriod? = transaction {
        SpendingPeriods.selectAll().where { SpendingPeriods.id eq id }
            .map { it }
            .singleOrNull()?.let { periodRow ->
                val entries = loadEntries(periodRow[SpendingPeriods.id])
                periodRow.toSpendingPeriod(entries)
            }
    }

    fun findByExternalId(externalId: String): Int? = transaction {
        SpendingPeriods.selectAll()
            .where { SpendingPeriods.externalId eq externalId }
            .map { it[SpendingPeriods.id] }
            .singleOrNull()
    }

    fun saveOrUpdate(period: SpendingPeriod): Int = transaction {
        val existingId = period.id?.let { findByExternalId(it) }
        if (existingId != null) {
            update(existingId, period)
            existingId
        } else {
            add(period)
        }
    }

    fun add(period: SpendingPeriod): Int = transaction {
        val pId = SpendingPeriods.insert {
            it[externalId] = period.id
            it[name] = period.name
            it[startDate] = period.startDate
            it[endDate] = period.endDate
            it[createdAt] = period.createdAt
        }[SpendingPeriods.id]
        
        saveEntries(pId, period.entries)
        pId
    }

    fun update(id: Int, period: SpendingPeriod): Boolean = transaction {
        val updated = SpendingPeriods.update({ SpendingPeriods.id eq id }) {
            it[externalId] = period.id
            it[name] = period.name
            it[startDate] = period.startDate
            it[endDate] = period.endDate
        } > 0
        
        if (updated) {
            PeriodEntries.deleteWhere { periodId eq id }
            saveEntries(id, period.entries)
        }
        updated
    }

    private fun saveEntries(pId: Int, entries: List<SpendingEntry>) {
        entries.forEach { entry ->
            val catId = getOrCreateCategory(entry.categoryName, entry.bucket)
            PeriodEntries.insert {
                it[periodId] = pId
                it[categoryId] = catId
                it[amount] = entry.amount
            }
        }
    }

    private fun getOrCreateCategory(name: String, bucket: String): Int {
        val existing = Categories.selectAll().where { Categories.name eq name }
            .map { it[Categories.id] }
            .singleOrNull()
        
        return existing ?: Categories.insert {
            it[Categories.name] = name
            it[Categories.bucket] = bucket
        }[Categories.id]
    }

    fun delete(id: Int): Boolean = transaction {
        SpendingPeriods.deleteWhere { SpendingPeriods.id eq id } > 0
    }

    fun getAllCategories(): List<CategoryDto> = transaction {
        Categories.selectAll().map {
            CategoryDto(it[Categories.id], it[Categories.name], it[Categories.bucket])
        }
    }

    fun updateCategoryBucket(categoryId: Int, bucket: String): Boolean = transaction {
        Categories.update({ Categories.id eq categoryId }) {
            it[Categories.bucket] = bucket
        } > 0
    }
}
