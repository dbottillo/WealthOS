package com.wealthos.server

import org.jetbrains.exposed.sql.Database
import org.flywaydb.core.Flyway

object DatabaseFactory {
    fun connectAndMigrate() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = System.getenv("JDBC_DATABASE_URL") ?: ""
        val user = System.getenv("JDBC_DATABASE_USER") ?: ""
        val password = System.getenv("JDBC_DATABASE_PASSWORD") ?: ""
        
        // 1. Run Migrations with Flyway
        val flyway = Flyway.configure()
            .dataSource(jdbcURL, user, password)
            .load()
        flyway.migrate()

        // 2. Connect Exposed to the database
        Database.connect(jdbcURL, driverClassName, user, password)
    }
}
