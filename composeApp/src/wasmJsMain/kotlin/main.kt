import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.wealthos.app.App
import com.wealthos.common.initKoin

import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val hostname = window.location.hostname
    val baseUrl = if (hostname == "localhost" || hostname == "127.0.0.1") {
        "http://localhost:8080"
    } else {
        "" // Relative path for production
    }

    initKoin(baseUrl = baseUrl) {
        // Web specific config
    }
    CanvasBasedWindow(title = "WealthOS", canvasElementId = "ComposeTarget") {
        App()
    }
}
