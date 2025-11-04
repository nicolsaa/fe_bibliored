package com.example.bibliored

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bibliored.ui.theme.BiblioRedTheme
import com.example.bibliored.view.SplashScreen
import com.example.bibliored.view.navigation.AppNav


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiblioRedTheme {
                AppNav()

            }
        }
    }
}