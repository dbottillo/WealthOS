# Cloudflare Access Authentication for WealthOS Desktop

This document outlines the design and implementation plan to authenticate the native macOS desktop app against Cloudflare Access using Service Tokens.

## Technical Strategy

To allow the native desktop application to bypass Cloudflare Access safely, we will utilize **Cloudflare Service Tokens**. 
A Service Token provides a `Client ID` and a `Client Secret` which must be included in the HTTP headers of every request sent to the API:
* `CF-Access-Client-Id`
* `CF-Access-Client-Secret`

### 1. Abstracting Auth Headers in KMP (`common` module)
We will define a platform-agnostic interface in the common module:
```kotlin
interface AuthHeadersProvider {
    fun getHeaders(): Map<String, String>
}
```

* **Web Target (`wasmJs`):** Since browser cookies handle authentication automatically, the web client will use an empty/no-op provider or pass `null` to Koin.
* **Desktop Target (`jvm`):** The JVM client will bind a `DesktopAuthHeadersProvider` that reads credentials directly from a local configuration file in the user's home folder: `~/.wealthos/auth.json`.

### 2. HttpClient Interceptor (`WealthOsClient.kt`)
The Ktor `HttpClient` in [WealthOsClient.kt](file:///Users/dbottillo/Development/WealthOS/common/src/commonMain/kotlin/com/wealthos/common/WealthOsClient.kt) will use the `defaultRequest` plugin to inject these headers if a provider is present.
We will also intercept responses: if an API request returns HTML instead of JSON, we throw a custom `CloudflareAuthException` (signaling that the request was redirected to the Cloudflare login page).

### 3. Error Propagation (`PeriodRepository.kt`)
Update [PeriodRepository.kt](file:///Users/dbottillo/Development/WealthOS/common/src/commonMain/kotlin/com/wealthos/common/PeriodRepository.kt) to rethrow network exceptions so the UI can display the error status.

---

## Verification
* Run the app without the config file: Verify the app shows a connection/auth error.
* Create `~/.wealthos/auth.json` with valid credentials: Run the app again and confirm it successfully fetches your data.

