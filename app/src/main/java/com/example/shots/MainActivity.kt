package com.example.espressoshots

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.espressoshots.data.ServiceLocator
import com.example.espressoshots.ui.app.AppNavHost
import com.example.espressoshots.ui.theme.ShotsTheme

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