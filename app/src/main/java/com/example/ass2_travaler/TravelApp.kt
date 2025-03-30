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
import com.example.ass2_travaler.screens.BudgetForm
import com.example.ass2_travaler.screens.DetailScreen
import com.example.ass2_travaler.screens.HomeCity
import com.example.ass2_travaler.viewmodel.HomeCityViewModel
import com.example.ass2_travaler.screens.TravelPlan
import com.example.ass2_travaler.screens.TravelPlanForm

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
                )
            ) {
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
                TravelPlan(navController, viewModel) // 修正参数顺序
            }
            composable(CityScreen.TravelPlanForm) {
                TravelPlanForm(
                    navController = navController,
                    planToEdit = null, // 新增模式
                    viewModel = viewModel
                )
            }
            composable(
                route = "${CityScreen.TravelPlanForm}/{planId}",
                arguments = listOf(navArgument("planId") { type = NavType.StringType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getString("planId")
                val planToEdit = viewModel.travelPlans.value?.find { it.id == planId }
                TravelPlanForm(
                    navController = navController,
                    planToEdit = planToEdit,
                    viewModel = viewModel
                )
            }
            composable(CityScreen.Budget.route) {
                Budget(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(
                route = "${CityScreen.BudgetForm.route}/{itemId}",
                arguments = listOf(navArgument("itemId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getLong("itemId") ?: -1L
                val itemToEdit = viewModel.items.value?.find { it.id == itemId }
                Log.d("NAV_DEBUG", "Entering BudgetForm with id: $itemId")
                BudgetForm(
                    viewModel = viewModel,
                    itemToEdit = if (itemId != -1L) itemToEdit else null,
                    onComplete = { navController.popBackStack() }
                )
            }
        }
    }
}