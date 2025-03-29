package com.example.ass2_travaler.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2_travaler.viewmodel.HomeCityViewModel

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2_travaler.data.TravelPlan
@Composable
fun TravelPlanForm(
    navController: NavController,
    planToEdit: TravelPlan? = null,
    viewModel: HomeCityViewModel = viewModel(factory = HomeCityViewModel.Factory)
) {
    var id by remember { mutableStateOf(planToEdit?.id ?: "") }
    var eventName by remember { mutableStateOf(planToEdit?.eventName ?: "") }
    var dateTimeStr by remember { mutableStateOf(planToEdit?.dateTime?.toString() ?: "") }
    var location by remember { mutableStateOf(planToEdit?.location ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = dateTimeStr,
            onValueChange = { dateTimeStr = it },
            label = { Text("Data time") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
//
        Button(onClick = {
            val dateTime = dateTimeStr.toLongOrNull() ?: System.currentTimeMillis()
            if (planToEdit != null) {

                val updatedPlan = TravelPlan(
                    id = planToEdit.id,
                    eventName = eventName,
                    dateTime = dateTime,
                    location = location
                )
                viewModel.updatePlan(updatedPlan)
            } else {
                // update plan
                viewModel.addPlan(TravelPlan(id, eventName, dateTime, location))
            }
            navController.popBackStack()
        }) {
            Text(if (planToEdit != null) "Save Changes" else "Submit")
        }
    }
    }

