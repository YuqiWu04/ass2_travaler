package com.example.ass2_travaler.screens
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2_travaler.data.TravelPlan
import com.example.ass2_travaler.viewmodel.HomeCityViewModel

@Composable
fun TravelPlan(viewModel: HomeCityViewModel= viewModel(factory = HomeCityViewModel.Factory)) {

    val travelPlans by viewModel.travelPlans.observeAsState(initial = emptyList())


    var id by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var dateTimeStr by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // 显示行程列表
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(travelPlans) { plan ->
                TravelPlanItem(plan = plan, onEdit = { updatedPlan ->
                    viewModel.updatePlan(updatedPlan)
                })
            }
        }

        // 表单区域
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
        Button(
            onClick = {
                // 解析日期时间，不成功则使用当前时间
                val dateTime = dateTimeStr.toLongOrNull() ?: System.currentTimeMillis()
                val plan = TravelPlan(id = id, eventName = eventName, dateTime = dateTime, location = location)
                viewModel.addPlan(plan)
                // 添加后清空输入
                id = ""
                eventName = ""
                dateTimeStr = ""
                location = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Add On")
        }
    }
}

@Composable
fun TravelPlanItem(plan: TravelPlan, onEdit: (TravelPlan) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "ID: ${plan.id}")
            Text(text = "Name: ${plan.eventName}")
            Text(text = "Time: ${plan.dateTime}")
            Text(text = "Location: ${plan.location}")

            Button(onClick = {

                onEdit(plan)
            }) {
                Text("Edit")
            }
        }
    }
}