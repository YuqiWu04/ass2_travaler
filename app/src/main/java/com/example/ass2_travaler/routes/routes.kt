package com.example.ass2_travaler.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector


enum class CityScreen(val route: String, val icon: ImageVector) {
    Listing("listing", Icons.Default.Home),
    TravelPlan("travel_plan", Icons.Default.Place),
    Budget("budget", Icons.Default.AccountBox),
    Detail("detail", Icons.Default.Info);
//    TravelPlanForm("travel_plan/add", Icons.Default.Add) // 新增表单路由
    companion object {
        const val TravelPlanForm = "travel_plan_form"
    }
}