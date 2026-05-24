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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.isSystemInDarkTheme
import com.wealthos.app.Screen

fun main() {
    System.setProperty("apple.awt.application.appearance", "system")
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
            
            val isDark = isSystemInDarkTheme()
            LaunchedEffect(isDark) {
                val appearance = if (isDark) "NSAppearanceNameDarkAqua" else "NSAppearanceNameAqua"
                window.rootPane.putClientProperty("apple.awt.windowAppearance", appearance)
                
                val bg = if (isDark) java.awt.Color(0x14, 0x12, 0x18) else java.awt.Color(0xF6, 0xF6, 0xF6)
                window.background = bg
                window.contentPane.background = bg
            }
            
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
                topPadding = 12.dp,
                showTopBar = false,
                externalScreen = currentScreen,
                onScreenChanged = { currentScreen = it }
            )
        }
    }
}
