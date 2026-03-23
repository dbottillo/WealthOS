package com.wealthos.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Database initialization (will fail if DB is not running, which is fine for now)
    try {
        DatabaseFactory.connectAndMigrate()
    } catch (e: Exception) {
        log.error("Failed to connect to database: ${e.message}")
    }

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "UP"))
        }
    }
}
