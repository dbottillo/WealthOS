import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.wealthos.app.App
import com.wealthos.common.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin(baseUrl = "http://localhost:8080") {
        // Web specific config
    }
    CanvasBasedWindow(title = "WealthOS", canvasElementId = "ComposeTarget") {
        App()
    }
}
