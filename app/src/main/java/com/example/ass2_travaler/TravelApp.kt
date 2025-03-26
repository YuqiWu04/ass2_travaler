package com.example.ass2_travaler

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ass2_travaler.routes.CityScreen
import com.example.ass2_travaler.screens.BottomNavBar
import com.example.ass2_travaler.screens.Budget
import com.example.ass2_travaler.screens.DetailScreen
import com.example.ass2_travaler.screens.HomeCity
import com.example.ass2_travaler.screens.HomeCityViewModel
import com.example.ass2_travaler.screens.TravelPlan

@SuppressLint("UnrememberedMutableState")
@Composable
fun TravelApp() {
    val navController = rememberNavController()
    val viewModel: HomeCityViewModel = viewModel(factory = HomeCityViewModel.Factory)



    Scaffold(
        bottomBar = {
            val currentMainRoute by derivedStateOf {
                navController.currentBackStackEntry
                    ?.destination
                    ?.route
                    ?.split("/")
                    ?.firstOrNull() ?: CityScreen.Listing.route
            }
            Log.d("NAV_DEBUG", "Resolved Main Route: $currentMainRoute")

            if (currentMainRoute in setOf(
                    CityScreen.Listing.route,
                    CityScreen.TravelPlan.route,
                    CityScreen.Budget.route
                )) {
                BottomNavBar(navController, viewModel)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = CityScreen.Listing.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(CityScreen.Listing.route) {
                HomeCity(navController, viewModel)
            }
            composable(
                route = "${CityScreen.Detail.route}/{cityId}",
                arguments = listOf(navArgument("cityId") { type = NavType.StringType })
            ) {
                DetailScreen(navController, viewModel)
            }
            composable(CityScreen.TravelPlan.route) {
                TravelPlan(navController, viewModel)
            }
            composable(CityScreen.Budget.route) {
                Budget(navController, viewModel)
            }
        }
    }
}