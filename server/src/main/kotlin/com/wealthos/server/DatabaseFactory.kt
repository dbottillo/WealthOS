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
        
        // Retry logic for initial connection
        var connected = false
        var retries = 5
        while (!connected && retries > 0) {
            try {
                // 1. Connect Exposed to the database
                Database.connect(jdbcURL, driverClassName, user, password)
                
                // Test the connection with a simple query
                org.jetbrains.exposed.sql.transactions.transaction {
                    org.jetbrains.exposed.sql.SchemaUtils.listTables()
                }
                
                connected = true
                println("Connected to database successfully.")
            } catch (e: Exception) {
                retries--
                println("Database not ready yet... Retrying in 5 seconds ($retries retries left)")
                Thread.sleep(5000)
                if (retries == 0) throw e
            }
        }

        // 2. Run Migrations with Flyway
        try {
            val flyway = Flyway.configure()
                .dataSource(jdbcURL, user, password)
                .baselineOnMigrate(true)
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
