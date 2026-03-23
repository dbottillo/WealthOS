package com.wealthos.server

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun connectAndMigrate() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = System.getenv("JDBC_DATABASE_URL") ?: ""
        val user = System.getenv("JDBC_DATABASE_USER") ?: ""
        val password = System.getenv("JDBC_DATABASE_PASSWORD") ?: ""
        
        val database = Database.connect(jdbcURL, driverClassName, user, password)
        
        transaction(database) {
            SchemaUtils.create(SpendingPeriods)
        }
    }
}
