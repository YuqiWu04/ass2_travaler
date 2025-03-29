package com.example.ass2_travaler.screens
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2_travaler.data.TravelPlan
import com.example.ass2_travaler.routes.CityScreen
import com.example.ass2_travaler.viewmodel.HomeCityViewModel

@Composable
fun TravelPlanItem(plan: TravelPlan, onEdit: (TravelPlan) -> Unit, onDelete: () -> Unit ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${plan.dateTime}  Weather:25-31c Cloudy", style = MaterialTheme.typography.bodySmall)
                IconButton(onClick = { /* 地图操作 */ }) {
                    Icon(Icons.Default.ThumbUp, "Map")
                }
            }


            Text("${plan.eventName}", style = MaterialTheme.typography.titleMedium)
            Text("Event number: ${plan.id}")


            Spacer(modifier = Modifier.height(8.dp))
            Text("Begin: ", style = MaterialTheme.typography.bodyMedium)
            Text("Time:${plan.dateTime} ")
            Text("Location: ${plan.location}")


            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { onEdit(plan) }) {
                    Text("EDIT")
                }
                Button(onClick = onDelete) {
                    Text("DELETE")
                }
            }
        }
    }
}
@Composable
fun TravelPlan(
    navController: NavController,
    viewModel: HomeCityViewModel = viewModel(factory = HomeCityViewModel.Factory)
) {
    val travelPlans by viewModel.travelPlans.observeAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Button(
            onClick = {
                navController.navigate(CityScreen.TravelPlanForm)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Plan")
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(travelPlans) { plan ->
                TravelPlanItem(
                    plan = plan,
                    onEdit = { planToEdit ->

                        navController.navigate(
                            "${CityScreen.TravelPlanForm}/${planToEdit.id}"
                        )
                    },
                    onDelete = { viewModel.deletePlan(plan) }
                )

            }
        }
    }
}
