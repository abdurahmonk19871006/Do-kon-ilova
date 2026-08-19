package com.company.qurilishmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.company.qurilishmarket.presentation.navigation.QurilishMarketNavHost
import com.company.qurilishmarket.presentation.theme.QurilishMarketTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QurilishMarketTheme {
                QurilishMarketNavHost()
            }
        }
    }
}
