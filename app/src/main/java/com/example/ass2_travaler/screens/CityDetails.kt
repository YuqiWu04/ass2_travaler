package com.example.ass2_travaler.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage


@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: HomeCityViewModel
) {
    // Getting Navigation Parameters
    val cityId = remember {
        navController.currentBackStackEntry
            ?.arguments
            ?.getString("cityId")
    }

    // Observing the UI state
    val uiState by viewModel.uiState.collectAsState()

    // Load data based on ID
    LaunchedEffect(cityId) {
        cityId?.let { viewModel.setCityId(it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeCityViewModel.CityUiState.Success -> {
                val city = state.city
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {

                    AsyncImage(
                        model = city.imageUrl[0],
                        contentDescription = city.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text(
                            text = city.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(title = "Top attraction:   ", value = city.top_attractions)
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(title = "Budget prediction", value = city.optimal_budget_usd.toString())
                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRow(title = "Rating", value = "${city.rating}/5")
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(title = "Best season", value = city.best_season)


                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = city.title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }


                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Return")
                    }
                }
            }
            is HomeCityViewModel.CityUiState.Error -> {
                ErrorView(message = state.message) {
                    navController.popBackStack()
                }
            }
            HomeCityViewModel.CityUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            HomeCityViewModel.CityUiState.Empty -> {
                Text(
                    text = "Please select city",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
private fun InfoRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("retry")
        }
    }
}
