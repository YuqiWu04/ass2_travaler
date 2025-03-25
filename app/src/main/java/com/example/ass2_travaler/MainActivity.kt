package com.example.ass2_travaler

import SharedTravelData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height

import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp

import com.example.ass2_travaler.ui.theme.Ass2_travalerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ass2_travalerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val isInitialized = remember { mutableStateOf(false) }
                    val coroutineScope = rememberCoroutineScope()

                    // Initialize SharedMovieData asynchronously
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            SharedTravelData.initialize(application)
                            isInitialized.value = true // Mark as initialized
                        }
                    }

                    // Show a loading screen until data is ready
                    if (isInitialized.value) {
                        TravelApp()
                    } else {
                        LoadingScreen()
                    }
                }
            }
        }
    }
}@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Loading movies...")
        }
    }
}