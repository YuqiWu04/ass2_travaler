package com.example.ass2_travaler.screens
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2_travaler.viewmodel.HomeCityViewModel
import androidx.compose.runtime.getValue

import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import com.example.ass2_travaler.componentTool.formatCurrency
import com.example.ass2_travaler.data.BudgetItem
import com.example.ass2_travaler.routes.CityScreen
import java.time.format.DateTimeFormatter

// screens/BudgetScreen.kt
@Composable
fun Budget(viewModel: HomeCityViewModel,navController: NavController ) {
    val items by viewModel.items.observeAsState(emptyList())
    val total by viewModel.totalSpending.observeAsState(0.0)
    val budgetLimit by viewModel.budgetLimit.observeAsState(0.0)
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        BudgetHeader(total, budgetLimit, { showDialog = true })
        CurrencyTransfer()
        Text("Budget control", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = {
                navController.navigate("${CityScreen.BudgetForm.route}/-1")
                Log.d("NAV_DEBUG", "Navigating to: ${CityScreen.BudgetForm.route}")
            }
        ) {
            Text("Add on")
        }
        LazyColumn {
            items(items) { item ->
                BudgetListItem(
                    item = item,
                    onEdit = {
                        navController.navigate("${CityScreen.BudgetForm.route}/${item.id}")
                    },
                    onDelete = { viewModel.deleteItem(item) }
                )
            }
        }
    }

    if (showDialog) {
        BudgetLimitDialog(
            currentLimit = budgetLimit,
            onConfirm = { viewModel.setBudgetLimit(it) },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun BudgetHeader(total: Double, limit: Double, onSetBudget: () -> Unit) {
    val progress = if (limit > 0 && !limit.isNaN()) {
        (total / limit).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            BudgetProgressBar(progress = progress)

            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = onSetBudget,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text("Setting budgetary limits")
                }
            }

            Text(
                text = "Total cost：${formatCurrency(total)}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
@Composable
fun BudgetListItem(
    item: BudgetItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {

                Text(
                    "Classification：${item.category}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Money：${formatCurrency(item.amount)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Date：${item.createdAt.format(DateTimeFormatter.ISO_DATE)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun BudgetLimitDialog(
    currentLimit: Double,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var limitInput by remember(currentLimit) {
        mutableStateOf(if (currentLimit > 0) currentLimit.toString() else "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Setting budgetary limits", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                Text("Current limit：${formatCurrency(currentLimit)}")
                TextField(
                    value = limitInput,
                    onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*\$"))) limitInput = it },
                    label = { Text("Input new limits") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    suffix = { Text("Unit: US") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newLimit = limitInput.toDoubleOrNull() ?: 0.0
                    if (newLimit > 0) onConfirm(newLimit)
                }
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
@Composable
fun BudgetProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(50)),
        color = MaterialTheme.colorScheme.primaryContainer,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}