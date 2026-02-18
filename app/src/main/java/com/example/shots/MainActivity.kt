package com.example.shots

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.shots.data.ServiceLocator
import com.example.shots.ui.app.AppNavHost
import com.example.shots.ui.theme.ShotsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShotsTheme {
                AppNavHost(repository = ServiceLocator.provideRepository(applicationContext))
            }
        }
    }
}

@Composable
fun PreviewApp() {
    ShotsTheme {
        // Preview placeholder
    }
}