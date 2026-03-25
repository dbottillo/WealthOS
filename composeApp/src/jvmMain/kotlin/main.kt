import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.wealthos.app.App
import com.wealthos.common.initKoin

fun main() = application {
    initKoin(baseUrl = "http://localhost:8080") {
        // Desktop specific config
    }
    Window(onCloseRequest = ::exitApplication, title = "WealthOS") {
        App()
    }
}
