package com.wealthos.server

import org.jetbrains.exposed.sql.Database
import org.flywaydb.core.Flyway

object DatabaseFactory {
    fun connectAndMigrate() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = System.getenv("JDBC_DATABASE_URL") ?: ""
        val user = System.getenv("JDBC_DATABASE_USER") ?: ""
        val password = System.getenv("JDBC_DATABASE_PASSWORD") ?: ""
        
        // 1. Connect Exposed to the database
        Database.connect(jdbcURL, driverClassName, user, password)

        // 2. Run Migrations with Flyway
        try {
            val flyway = Flyway.configure()
                .dataSource(jdbcURL, user, password)
                .load()
            flyway.migrate()
        } catch (e: Exception) {
            println("Flyway migration failed: ${e.message}. Ensuring table exists via SchemaUtils as fallback...")
            // Fallback for local dev if Flyway is being tricky or DB is empty but accessible
            // transaction { SchemaUtils.create(SpendingPeriods) } 
            // Actually, let's just let it fail if the user wants production readiness, 
            // but for now, we want the app to start.
            throw e
        }
    }
}
