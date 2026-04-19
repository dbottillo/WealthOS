package com.wealthos.server

import com.wealthos.common.SpendingPeriod
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.json.*

class NotionMigrationService(
    private val notionApiKey: String,
    private val databaseId: String,
    private val repository: SpendingPeriodRepository
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun migrate() {
        var hasMore = true
        var nextCursor: String? = null

        while (hasMore) {
            val response = client.post("https://api.notion.com/v1/databases/$databaseId/query") {
                header("Authorization", "Bearer $notionApiKey")
                header("Notion-Version", "2022-06-28")
                contentType(ContentType.Application.Json)
                setBody(buildJsonObject {
                    if (nextCursor != null) {
                        put("start_cursor", nextCursor)
                    }
                })
            }.body<JsonObject>()

            val results = response["results"]?.jsonArray ?: emptyList()
            results.forEach { page ->
                val pageObj = page.jsonObject
                val properties = pageObj["properties"]?.jsonObject ?: return@forEach
                val id = pageObj["id"]?.jsonPrimitive?.content ?: ""
                val spendingPeriod = mapPageToSpendingPeriod(id, properties)
                repository.saveOrUpdate(spendingPeriod)
            }

            hasMore = response["has_more"]?.jsonPrimitive?.boolean ?: false
            nextCursor = response["next_cursor"]?.jsonPrimitive?.contentOrNull
        }
    }

    private fun mapPageToSpendingPeriod(id: String, properties: JsonObject): SpendingPeriod {
        fun getNumber(name: String): Double = properties[name]?.jsonObject?.get("number")?.jsonPrimitive?.doubleOrNull ?: 0.0
        fun getTitle(name: String): String = properties[name]?.jsonObject?.get("title")?.jsonArray?.firstOrNull()?.jsonObject?.get("plain_text")?.jsonPrimitive?.content ?: "Unknown"
        fun getDateStart(name: String): String = properties[name]?.jsonObject?.get("date")?.jsonObject?.get("start")?.jsonPrimitive?.content ?: "1970-01-01"
        fun getDateEnd(name: String): String = properties[name]?.jsonObject?.get("date")?.jsonObject?.get("end")?.jsonPrimitive?.content ?: "1970-01-01"
        fun getCreatedTime(name: String): String = properties[name]?.jsonObject?.get("created_time")?.jsonPrimitive?.content ?: "1970-01-01T00:00:00Z"

        return SpendingPeriod(
            id = id,
            name = getTitle("Name"),
            startDate = getDateStart("Period").toLocalDate(),
            endDate = getDateEnd("Period").toLocalDate(),
            createdAt = getCreatedTime("Created time").toInstant(),
            salary = getNumber("Salary"),
            otherIncome = getNumber("Other income"),
            partnerContributions = getNumber("Fabio contributions"),
            mortgage = getNumber("Mortgage"),
            bills = getNumber("Bills"),
            groceries = getNumber("Groceries"),
            transport = getNumber("Transport"),
            personalCare = getNumber("Personal care"),
            dentist = getNumber("Dentist"),
            expenses = getNumber("Expenses"),
            eatingOut = getNumber("Eating out"),
            shopping = getNumber("Shopping"),
            entertainment = getNumber("Entertainment"),
            books = getNumber("Books"),
            clothing = getNumber("Clothing"),
            gifts = getNumber("Gifts"),
            tech = getNumber("Tech"),
            drinks = getNumber("Drinks"),
            holidays = getNumber("Holidays"),
            lego = getNumber("Lego"),
            gaming = getNumber("Gaming"),
            comics = getNumber("Comics"),
            psychotherapy = getNumber("Psycotherapy"),
            gym = getNumber("Gym"),
            cycling = getNumber("Cycling"),
            culture = getNumber("Culture"),
            parents = getNumber("Parents"),
            savings = getNumber("Savings"),
            investment = getNumber("Investment"),
            sipp = getNumber("SIPP")
        )
    }
}
