package com.wealthos.common

interface AuthHeadersProvider {
    fun getHeaders(): Map<String, String>
}

class CloudflareAuthException(message: String) : Exception(message)
