import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.MenuBar
import com.wealthos.app.App
import com.wealthos.common.initKoin

import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.wealthos.app.Screen

fun main() {
    System.setProperty("apple.awt.application.name", "WealthOS")
    application {
        val baseUrl = System.getenv("WEALTHOS_API_URL") ?: "https://wealthos.bottillo.com"
        initKoin(baseUrl = baseUrl) {
            // Desktop specific config
        }
        Window(onCloseRequest = ::exitApplication, title = "WealthOS") {
            window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
            window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            window.rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
            
            var currentScreen by remember { mutableStateOf(Screen.List) }
            
            MenuBar {
                Menu("Navigation") {
                    Item("Overview", onClick = { currentScreen = Screen.List })
                    Item("Analytics", onClick = { currentScreen = Screen.Analytics })
                    Item("Manage Categories", onClick = { currentScreen = Screen.Categories })
                }
            }
            
            App(
                showTitle = false,
                topPadding = 28.dp,
                showTopBar = false,
                externalScreen = currentScreen,
                onScreenChanged = { currentScreen = it }
            )
        }
    }
}
