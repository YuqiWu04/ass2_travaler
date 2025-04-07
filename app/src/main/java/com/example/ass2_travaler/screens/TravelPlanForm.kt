package com.example.ass2_travaler.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ass2_travaler.data.TravelPlan
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPlanForm(
    navController: NavController,
    planToEdit: TravelPlan? = null,
    viewModel: HomeCityViewModel = viewModel(factory = HomeCityViewModel.Factory)
) {
    val context = LocalContext.current
    var id by remember { mutableStateOf(planToEdit?.id ?: "") }
    var eventName by remember { mutableStateOf(planToEdit?.eventName ?: "") }
    var dateTimeStr by remember { mutableStateOf(planToEdit?.dateTime?.toString() ?: "") }
    var location by remember { mutableStateOf(planToEdit?.location ?: "") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = if (planToEdit != null) "Edit Travel Plan" else "New Travel Plan",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dateTimeStr,
                onValueChange = { dateTimeStr = it },
                label = { Text("Date Time") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (id.isBlank() || eventName.isBlank() || dateTimeStr.isBlank() || location.isBlank()) {
                        Toast.makeText(context, "Please enter your plan details", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val dateTime = dateTimeStr.toLongOrNull() ?: System.currentTimeMillis()
                    if (planToEdit != null) {
                        viewModel.updatePlan(
                            planToEdit.copy(
                                eventName = eventName,
                                dateTime = dateTime,
                                location = location
                            )
                        )
                    } else {
                        viewModel.addPlan(TravelPlan(id, eventName, dateTime, location))
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (planToEdit != null) "Save Changes" else "Submit")
            }
        }
    }
}