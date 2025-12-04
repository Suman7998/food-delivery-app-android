package com.munchmatch.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.munchmatch.app.ui.theme.MunchMatchTheme
import com.munchmatch.app.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MunchMatchTheme {
                Surface { AppNavGraph() }
            }
        }
    }
}
