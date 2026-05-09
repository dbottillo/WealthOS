import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.wealthos.app.App
import com.wealthos.common.initKoin

import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val hostname = window.location.hostname
    val origin = window.location.origin
    val baseUrl = if (hostname == "localhost" || hostname == "127.0.0.1") {
        "http://localhost:8080"
    } else {
        origin // Use the same origin for production (e.g., https://wealthos.bottillo.com)
    }

    // Allow browser context menu (right-click)
    window.addEventListener("contextmenu", { event ->
        // By not calling event.preventDefault(), we let the browser handle it
    }, true)

    initKoin(baseUrl = baseUrl) {
        // Web specific config
    }
    CanvasBasedWindow(title = "WealthOS", canvasElementId = "ComposeTarget") {
        App()
    }
}
