package com.wealthos.server

import org.jetbrains.exposed.sql.Database
import org.flywaydb.core.Flyway

object DatabaseFactory {
    fun connectAndMigrate() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = System.getenv("JDBC_DATABASE_URL") ?: ""
        val user = System.getenv("JDBC_DATABASE_USER") ?: ""
        val password = System.getenv("JDBC_DATABASE_PASSWORD") ?: ""
        
        println("Connecting to database: $jdbcURL (User: $user)")
        
        // 1. Connect Exposed to the database
        Database.connect(jdbcURL, driverClassName, user, password)

        // 2. Run Migrations with Flyway
        try {
            val flyway = Flyway.configure()
                .dataSource(jdbcURL, user, password)
                .baselineOnMigrate(true) // Added this to handle existing DBs
                .load()
            
            println("Running Flyway migrations...")
            val result = flyway.migrate()
            println("Flyway migration result: ${result.migrationsExecuted} migrations executed.")
        } catch (e: Exception) {
            println("ERROR: Flyway migration failed: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
}
