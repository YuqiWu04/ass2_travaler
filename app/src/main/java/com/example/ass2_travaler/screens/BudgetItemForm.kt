package com.example.ass2_travaler.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.ass2_travaler.viewmodel.HomeCityViewModel
import java.time.LocalDate

import androidx.compose.material3.Button

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.focusable

import androidx.compose.ui.focus.focusRequester

import androidx.compose.runtime.getValue

import androidx.compose.runtime.setValue

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton

import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.unit.dp
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

import androidx.compose.ui.text.input.KeyboardType
import com.example.ass2_travaler.data.BudgetItem
import java.time.Instant
import java.time.ZoneId

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Food", "Transport", "Lodging", "Entertainment", "Shopping")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            label = { Text("Classification") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
// screens/BudgetForm.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetForm(
    viewModel: HomeCityViewModel,
    itemToEdit: BudgetItem? = null,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var category by remember { mutableStateOf(itemToEdit?.category ?: "") }
    var amount by remember { mutableStateOf(itemToEdit?.amount?.toString() ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }

    val initialDate = itemToEdit?.createdAt?.let {
        LocalDate.parse(it).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } ?: System.currentTimeMillis()


    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)


    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?: LocalDate.now()


    Surface(
        modifier = Modifier
            .fillMaxWidth()
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
                text = if (itemToEdit != null) "Update Budget Item" else "Add Budget Item",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            CategoryDropdown(
                selectedCategory = category,
                onCategorySelected = { category = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selected Date: ${selectedDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {showDatePicker = true

                    }
                ) {
                    Text("Pick Date")
                }
            }

            Button(
                onClick = {
                    if (category.isBlank() || amount.isBlank()) {
                        Toast.makeText(context, "Please enter your item details!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // Using datePickerState selectedDateMillis update date
                    val selectedLocalDate = datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    } ?: LocalDate.now()
                    if (itemToEdit != null) {
                        viewModel.updateItem(
                            itemToEdit.copy(
                                category = category,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                createdAt = selectedLocalDate.toString()
                            )
                        )
                    } else {
                        viewModel.addItem(
                            BudgetItem(
                                category = category,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                createdAt = selectedLocalDate.toString()
                            )
                        )
                    }
                    onComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (itemToEdit != null) "Update" else "Save")
            }
        }
    }
    // Displays the DatePickerDialog when showDatePicker is true
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}