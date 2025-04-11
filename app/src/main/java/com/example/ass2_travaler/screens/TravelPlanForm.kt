package com.example.ass2_travaler.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    var location by remember { mutableStateOf(planToEdit?.location ?: "") }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    var selectedDateTime by remember { mutableStateOf(planToEdit?.dateTime ?: System.currentTimeMillis()) }

    var dateTimeStr by remember { mutableStateOf(dateFormat.format(Date(selectedDateTime))) }


    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDateTime }
    fun onPickDateTime() {
        showDateTimePicker(context, selectedDateTime) { newTime ->
            selectedDateTime = newTime
            dateTimeStr = dateFormat.format(Date(newTime))
        }
    }
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
                onValueChange = {  },
                label = { Text("Date Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPickDateTime() },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { onPickDateTime() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.DateRange,
                            contentDescription = "Select date/time"
                        )
                    }
                }
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
                    val dateTime = try {
                        dateFormat.parse(dateTimeStr)?.time ?: throw Exception("Parse error")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Invalid date/time format. Please use yyyy-MM-dd HH:mm", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
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
fun showDateTimePicker(
    context: Context,
    initialTime: Long,
    onDateTimeSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = initialTime }
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    onDateTimeSelected(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}