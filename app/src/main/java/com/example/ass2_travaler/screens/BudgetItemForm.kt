package com.example.ass2_travaler.screens

import android.R.attr.id
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.ass2_travaler.viewmodel.HomeCityViewModel
import java.time.LocalDate

import androidx.compose.material3.Button

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


import androidx.compose.runtime.getValue

import androidx.compose.runtime.setValue

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState

import androidx.compose.ui.text.input.KeyboardType
import com.example.ass2_travaler.data.BudgetItem
import java.time.Instant
import java.time.ZoneId


// screens/BudgetForm.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetForm(
    viewModel: HomeCityViewModel,
    itemToEdit: BudgetItem? = null,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var category by remember {
        mutableStateOf(itemToEdit?.category ?: "")
    }
    var amount by remember {
        mutableStateOf(itemToEdit?.amount.toString() ?: "")
    }


    val initialDate = itemToEdit?.createdAt?.let {
        LocalDate.parse(it).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } ?: System.currentTimeMillis()

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Classification") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )


        Button(
            onClick = {
                if (category.isBlank() || amount.isBlank()) {
                    //avoid empty
                    Toast.makeText(context,"Please Enter your item details!!" ,Toast.LENGTH_SHORT).show()
                    return@Button
                }
                val selectedDate = datePickerState.selectedDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                } ?: LocalDate.now()


                if (itemToEdit != null) {
                    viewModel.updateItem(
                        itemToEdit.copy(
                            category = category,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            createdAt = selectedDate.toString()
                        )
                    )
                } else {
                    viewModel.addItem(
                        BudgetItem(
                            category = category,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            createdAt = selectedDate.toString()
                        )
                    )
                }
                onComplete()
            }
        ) {
            Text(if (itemToEdit != null) "Update" else "Save")
        }
    }
}