package com.example.ass2_travaler.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.material3.Icon

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ass2_travaler.routes.CityScreen
import com.example.ass2_travaler.viewmodel.HomeCityViewModel

@Composable
fun BottomNavBar(navController: NavHostController, viewModel: HomeCityViewModel) {
    val navBackStackEntryState = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntryState.value?.destination?.route
    Log.d("NAV_DEBUG", "currentRoute: $currentRoute")
    CityScreen.entries.forEach { screen ->
        Log.d("NAV_DEBUG", "check route: ${screen.route}, result: ${currentRoute == screen.route}")
    }
    NavigationBar( modifier = Modifier.height(110.dp).fillMaxWidth(), containerColor = Color(0xFFDCEDC8),
        contentColor = Color.White) {
        CityScreen.entries
            .filter { it in setOf(CityScreen.Listing, CityScreen.TravelPlan, CityScreen.Budget) }
            .forEach { screen ->
                NavigationBarItem(
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().route!!) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.route.replaceFirstChar { it.uppercase() }) }
                )
            }
    }
}