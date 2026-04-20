package com.wealthos.server

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.wealthos.common.toDto

fun main() {
    println("Starting server on http://localhost:8080...")
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module(SpendingPeriodRepository())
    }.start(wait = true)
}

fun Application.module(repository: SpendingPeriodRepository) {
    install(CORS) {
        anyHost() // Allow access from any host (important for Docker/Nginx proxying)
        allowMethod(io.ktor.http.HttpMethod.Get)
        allowMethod(io.ktor.http.HttpMethod.Post)
        allowMethod(io.ktor.http.HttpMethod.Put)
        allowMethod(io.ktor.http.HttpMethod.Delete)
        allowMethod(io.ktor.http.HttpMethod.Options)
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
    }

    // Database initialization
    try {
        DatabaseFactory.connectAndMigrate()
    } catch (e: Exception) {
        log.error("Failed to connect to database: ${e.message}")
        println("ERROR: Failed to connect to database: ${e.message}")
    }

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/health") {
            call.respond(mapOf("status" to "UP"))
        }

        apiRoutes(repository)

        post("/api/migrate") {
            val apiKey = System.getenv("NOTION_API_KEY") ?: ""
            val databaseId = System.getenv("NOTION_DATABASE_ID") ?: "9a1f95e6fdeb42db9cf4690bc97aab8d"
            
            println("Migration triggered. API Key present: ${apiKey.isNotEmpty()}, Database ID: $databaseId")
            
            if (apiKey.isEmpty()) {
                call.respond(io.ktor.http.HttpStatusCode.InternalServerError, "Notion API key not set")
                return@post
            }
            try {
                NotionMigrationService(apiKey, databaseId, repository).migrate()
                call.respond(io.ktor.http.HttpStatusCode.OK, "Migration successful")
            } catch (e: Exception) {
                println("ERROR: Migration failed: ${e.message}")
                e.printStackTrace()
                call.respond(io.ktor.http.HttpStatusCode.InternalServerError, "Migration failed: ${e.message}")
            }
        }
    }
}

fun Route.apiRoutes(repository: SpendingPeriodRepository) {
    route("/api/periods") {
        get {
            call.respond(repository.findAll().map { it.toDto() })
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }
            val period = repository.findById(id)
            if (period == null) {
                call.respond(io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(period.toDto())
            }
        }
        post {
            val period = call.receive<com.wealthos.common.SpendingPeriod>()
            val id = repository.add(period)
            call.respond(io.ktor.http.HttpStatusCode.Created, mapOf("id" to id))
        }
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(io.ktor.http.HttpStatusCode.BadRequest)
                return@put
            }
            val period = call.receive<com.wealthos.common.SpendingPeriod>()
            if (repository.update(id, period)) {
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
            if (repository.delete(id)) {
                call.respond(io.ktor.http.HttpStatusCode.OK)
            } else {
                call.respond(io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }
}
