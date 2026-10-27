package com.goldensales.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val Gold = Color(0xFFC69A36)
private val DarkGreen = Color(0xFF153C32)
private val Background = Color(0xFFF7F7F2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = DarkGreen,
                    secondary = Gold,
                    background = Background,
                    surface = Color.White
                )
            ) {
                GoldenSalesApp()
            }
        }
    }
}