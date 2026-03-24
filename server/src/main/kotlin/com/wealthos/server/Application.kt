package com.wealthos.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.wealthos.common.toDto

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

        route("/api/periods") {

            get {
                call.respond(SpendingPeriodRepository.findAll().map { it.toDto() })
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(io.ktor.http.HttpStatusCode.BadRequest)
                    return@get
                }
                val period = SpendingPeriodRepository.findById(id)
                if (period == null) {
                    call.respond(io.ktor.http.HttpStatusCode.NotFound)
                } else {
                    call.respond(period.toDto())
                }
            }
            post {
                val period = call.receive<com.wealthos.common.SpendingPeriod>()
                val id = SpendingPeriodRepository.add(period)
                call.respond(io.ktor.http.HttpStatusCode.Created, mapOf("id" to id))
            }
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(io.ktor.http.HttpStatusCode.BadRequest)
                    return@put
                }
                val period = call.receive<com.wealthos.common.SpendingPeriod>()
                if (SpendingPeriodRepository.update(id, period)) {
                    call.respond(io.ktor.http.HttpStatusCode.OK)
                } else {
                    call.respond(io.ktor.http.HttpStatusCode.NotFound)
                }
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(io.ktor.http.HttpStatusCode.BadRequest)
                    return@delete
                }
                if (SpendingPeriodRepository.delete(id)) {
                    call.respond(io.ktor.http.HttpStatusCode.OK)
                } else {
                    call.respond(io.ktor.http.HttpStatusCode.NotFound)
                }
            }
        }
    }
}
