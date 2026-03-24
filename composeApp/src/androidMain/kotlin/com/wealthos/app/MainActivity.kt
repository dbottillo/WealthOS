package com.wealthos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wealthos.common.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // In a real app, this might be in an Application class
        initKoin(baseUrl = "http://10.0.2.2:8080") {
            // Android specific config
        }

        setContent {
            App()
        }
    }
}
