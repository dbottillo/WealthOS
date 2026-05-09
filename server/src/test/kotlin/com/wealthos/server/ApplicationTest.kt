package com.wealthos.server

import com.wealthos.common.SpendingPeriod
import com.wealthos.common.SpendingEntry
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class ApplicationTest {

    private val repository = SpendingPeriodRepository()

    @BeforeTest
    fun setup() {
        // Initialize H2 In-Memory Database for tests
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.drop(SpendingPeriods, Categories, PeriodEntries) // Clean start
            SchemaUtils.create(SpendingPeriods, Categories, PeriodEntries)
        }
    }

    private fun wealthOsTestApplication(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            module(repository)
        }
        block()
    }

    @Test
    fun testHealth() = wealthOsTestApplication {
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{\"status\":\"UP\"}", response.bodyAsText())
    }

    @Test
    fun testGetPeriodsEmpty() = wealthOsTestApplication {
        val response = client.get("/api/periods")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }

    @Test
    fun testCreateAndGetPeriod() = wealthOsTestApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val period = SpendingPeriod(
            name = "Jan 2026",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 31),
            createdAt = Clock.System.now(),
            entries = listOf(SpendingEntry("Salary", 5000.0, "INCOME"))
        )

        // Create
        val postResponse = client.post("/api/periods") {
            contentType(ContentType.Application.Json)
            setBody(period)
        }
        assertEquals(HttpStatusCode.Created, postResponse.status)
        val body = Json.decodeFromString<JsonObject>(postResponse.bodyAsText())
        val id = body["id"]?.jsonPrimitive?.int ?: -1
        assertTrue(id > 0)

        // Get All
        val getResponse = client.get("/api/periods")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val periods = Json.decodeFromString<JsonArray>(getResponse.bodyAsText())
        assertEquals(1, periods.size)
        assertEquals("Jan 2026", periods[0].jsonObject["name"]?.jsonPrimitive?.content)

        // Get By ID
        val getByIdResponse = client.get("/api/periods/$id")
        assertEquals(HttpStatusCode.OK, getByIdResponse.status)
        assertEquals("Jan 2026", Json.decodeFromString<JsonObject>(getByIdResponse.bodyAsText())["name"]?.jsonPrimitive?.content)
    }

    @Test
    fun testUpdateAndDeletePeriod() = wealthOsTestApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val period = SpendingPeriod(
            name = "Old Name",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 31),
            createdAt = Clock.System.now(),
            entries = emptyList()
        )

        val postResponse = client.post("/api/periods") {
            contentType(ContentType.Application.Json)
            setBody(period)
        }
        val id = Json.decodeFromString<JsonObject>(postResponse.bodyAsText())["id"]?.jsonPrimitive?.int ?: -1

        // Update
        val updatedPeriod = period.copy(name = "New Name")
        val putResponse = client.put("/api/periods/$id") {
            contentType(ContentType.Application.Json)
            setBody(updatedPeriod)
        }
        assertEquals(HttpStatusCode.OK, putResponse.status)

        val getResponse = client.get("/api/periods/$id")
        assertEquals("New Name", Json.decodeFromString<JsonObject>(getResponse.bodyAsText())["name"]?.jsonPrimitive?.content)

        // Delete
        val deleteResponse = client.delete("/api/periods/$id")
        assertEquals(HttpStatusCode.OK, deleteResponse.status)

        val getAfterDelete = client.get("/api/periods/$id")
        assertEquals(HttpStatusCode.NotFound, getAfterDelete.status)
    }

    @Test
    fun testCategoryManagement() = wealthOsTestApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        // Add a period with a new category
        val period = SpendingPeriod(
            name = "Category Test",
            startDate = LocalDate(2026, 1, 1),
            endDate = LocalDate(2026, 1, 31),
            createdAt = Clock.System.now(),
            entries = listOf(SpendingEntry("New Category", 100.0, "UNCATEGORIZED"))
        )
        client.post("/api/periods") {
            contentType(ContentType.Application.Json)
            setBody(period)
        }

        // Get categories
        val categoriesResponse = client.get("/api/categories")
        assertEquals(HttpStatusCode.OK, categoriesResponse.status)
        val categories = Json.decodeFromString<JsonArray>(categoriesResponse.bodyAsText())
        val newCat = categories.find { it.jsonObject["name"]?.jsonPrimitive?.content == "New Category" }
        assertNotNull(newCat)
        assertEquals("UNCATEGORIZED", newCat.jsonObject["bucket"]?.jsonPrimitive?.content)

        val catId = newCat.jsonObject["id"]?.jsonPrimitive?.int ?: -1

        // Update bucket
        val updateResponse = client.put("/api/categories/$catId/bucket") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("bucket" to "WANT"))
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        // Verify update
        val categoriesResponse2 = client.get("/api/categories")
        val updatedCat = Json.decodeFromString<JsonArray>(categoriesResponse2.bodyAsText()).find { it.jsonObject["id"]?.jsonPrimitive?.int == catId }
        assertEquals("WANT", updatedCat?.jsonObject["bucket"]?.jsonPrimitive?.content)
    }
}
