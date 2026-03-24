package com.wealthos.common

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class WealthOsClient(private val baseUrl: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getHealth(): Map<String, String> {
        return client.get("$baseUrl/health").body()
    }

    suspend fun getPeriods(): List<SpendingPeriodDto> {
        return client.get("$baseUrl/api/periods").body()
    }

    suspend fun addPeriod(period: SpendingPeriod): Int {
        val response: Map<String, Int> = client.post("$baseUrl/api/periods") {
            contentType(ContentType.Application.Json)
            setBody(period)
        }.body()
        return response["id"] ?: -1
    }

    suspend fun updatePeriod(id: Int, period: SpendingPeriod) {
        client.put("$baseUrl/api/periods/$id") {
            contentType(ContentType.Application.Json)
            setBody(period)
        }
    }

    suspend fun deletePeriod(id: Int) {
        client.delete("$baseUrl/api/periods/$id")
    }

    suspend fun triggerMigration() {
        client.post("$baseUrl/api/migrate")
    }
}
