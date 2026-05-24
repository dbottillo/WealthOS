package com.wealthos.app

import com.wealthos.common.AuthHeadersProvider
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
private data class AuthConfig(val clientId: String, val clientSecret: String)

class DesktopAuthHeadersProvider : AuthHeadersProvider {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val configFile: File
        get() {
            val dir = File(System.getProperty("user.home"), ".wealthos")
            if (!dir.exists()) dir.mkdirs()
            return File(dir, "auth.json")
        }

    override fun getHeaders(): Map<String, String> {
        val file = configFile
        if (!file.exists()) {
            println("Auth config file not found at: ${file.absolutePath}")
            return emptyMap()
        }
        return try {
            val config = json.decodeFromString(AuthConfig.serializer(), file.readText())
            println("Loaded Cloudflare Access Service Token from ${file.name} (Client ID starting with: ${config.clientId.take(8)}...)")
            mapOf(
                "CF-Access-Client-Id" to config.clientId,
                "CF-Access-Client-Secret" to config.clientSecret
            )
        } catch (e: Exception) {
            println("Error loading/parsing auth.json:")
            e.printStackTrace()
            emptyMap()
        }
    }
}
