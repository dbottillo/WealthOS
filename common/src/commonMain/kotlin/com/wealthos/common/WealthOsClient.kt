package com.wealthos.common

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class WealthOsClient(
    private val baseUrl: String = "",
    private val authHeadersProvider: AuthHeadersProvider? = null
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        if (authHeadersProvider != null) {
            defaultRequest {
                authHeadersProvider.getHeaders().forEach { (key, value) ->
                    header(key, value)
                }
            }
        }
    }

    private fun getUrl(path: String): String {
        return if (baseUrl.isBlank()) path else "$baseUrl$path"
    }

    private fun HttpResponse.verifySuccess() {
        if (contentType()?.match(ContentType.Text.Html) == true) {
            throw CloudflareAuthException("Cloudflare Access authentication required")
        }
    }

    suspend fun getHealth(): Map<String, String> {
        val response = client.get(getUrl("/health"))
        response.verifySuccess()
        return response.body()
    }

    suspend fun getPeriods(): List<SpendingPeriodDto> {
        val response = client.get(getUrl("/api/periods"))
        response.verifySuccess()
        return response.body()
    }

    suspend fun addPeriod(period: SpendingPeriod): Int {
        val response = client.post(getUrl("/api/periods")) {
            contentType(ContentType.Application.Json)
            setBody(period)
        }
        response.verifySuccess()
        val responseBody: Map<String, Int> = response.body()
        return responseBody["id"] ?: -1
    }

    suspend fun updatePeriod(id: Int, period: SpendingPeriod) {
        val response = client.put(getUrl("/api/periods/$id")) {
            contentType(ContentType.Application.Json)
            setBody(period)
        }
        response.verifySuccess()
    }

    suspend fun deletePeriod(id: Int) {
        val response = client.delete(getUrl("/api/periods/$id"))
        response.verifySuccess()
    }

    suspend fun triggerMigration() {
        val response = client.post(getUrl("/api/migrate"))
        response.verifySuccess()
    }

    suspend fun getCategories(): List<CategoryDto> {
        val response = client.get(getUrl("/api/categories"))
        response.verifySuccess()
        return response.body()
    }

    suspend fun updateCategoryBucket(id: Int, bucket: String) {
        val response = client.put(getUrl("/api/categories/$id/bucket")) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("bucket" to bucket))
        }
        response.verifySuccess()
    }
}
