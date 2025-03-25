package com.example.ass2_travaler

import SharedTravelData
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ass2_travaler.routes.CityScreen
import com.example.ass2_travaler.screens.DetailScreen
import com.example.ass2_travaler.screens.HomeCity
import com.example.ass2_travaler.screens.HomeCityViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TravelApp() {
    val navController: NavHostController = rememberNavController()
    val sharedViewModel: HomeCityViewModel = viewModel(factory = HomeCityViewModel.Factory)

    Log.d("Checking", "Shared data load "+ SharedTravelData.cities.value.size.toString())
    NavHost(
        navController = navController,
        startDestination = CityScreen.Listing.name
    ) {
        composable(route = CityScreen.Listing.name) {
            HomeCity(navController,sharedViewModel)
        }
        composable(
            route = "${CityScreen.Detail.name}/{cityId}",
            arguments = listOf(navArgument("cityId") { type = NavType.StringType })
        ) {
            DetailScreen(navController, sharedViewModel)
        }

    }

}