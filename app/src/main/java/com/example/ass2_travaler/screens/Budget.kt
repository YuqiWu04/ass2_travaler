package com.example.ass2_travaler.screens


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass2_travaler.componentTool.formatCurrency
import com.example.ass2_travaler.data.BudgetItem
import com.example.ass2_travaler.routes.CityScreen
import com.example.ass2_travaler.viewmodel.HomeCityViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun Budget(viewModel: HomeCityViewModel, navController: NavController) {
    val items by viewModel.items.observeAsState(emptyList())
    val total by viewModel.totalSpending.observeAsState(0.0)
    val budgetLimit by viewModel.budgetLimit.observeAsState(0.0)

    val aggregatedData by viewModel.aggregatedBudgetData.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Budget Control",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    BudgetHeader(
                        total = total,
                        limit = budgetLimit,
                        onSetBudget = { showDialog = true }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Currency Transfer",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    CurrencyTransfer()
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    BudgetLineChart(
                        aggregatedData = aggregatedData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            navController.navigate("${CityScreen.BudgetForm.route}/-1")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add Budget Item")
                    }
                }
            }
        }

        items(items) { item ->
            BudgetListItem(
                item = item,
                onEdit = { navController.navigate("${CityScreen.BudgetForm.route}/${item.id}") },
                onDelete = { viewModel.deleteItem(item) }
            )
        }
    }

    if (showDialog) {
        BudgetLimitDialog(
            currentLimit = budgetLimit,
            onConfirm = { newLimit ->
                viewModel.setBudgetLimit(newLimit)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}
@Composable
fun BudgetHeader(total: Double, limit: Double, onSetBudget: () -> Unit) {
    // When limit > 0 and is not NaN, progress is calculated based on total expenditure and limit
    val progress = if (limit > 0 && !limit.isNaN()) {
        (total / limit).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    val isOverLimit = limit > 0 && total > limit

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            BudgetProgressBar(
                progress = progress,
                progressColor = if (isOverLimit) Color.Red else MaterialTheme.colorScheme.primary
            )

            if (isOverLimit) {
                Text(
                    text = "Budget limit exceeded!",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Button(
                    onClick = onSetBudget,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                ) {
                    Text("Set Budget Limitation")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total: ${formatCurrency(total)}",
                style = MaterialTheme.typography.bodyLarge,
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
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Category: ${item.category}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Amount: ${formatCurrency(item.amount)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Date: ${item.createdAt.format(DateTimeFormatter.ISO_DATE)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
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
        title = {
            Text(
                "Set Budget Limit",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    "Current limit: ${formatCurrency(currentLimit)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = limitInput,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*\$"))) limitInput = it
                    },
                    label = { Text("New limit") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newLimit = limitInput.toDoubleOrNull() ?: 0.0
                    if (newLimit > 0) onConfirm(newLimit)
                },
                shape = RoundedCornerShape(12.dp)
            ) { Text("Confirm") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun BudgetProgressBar(
    progress: Float,
    progressColor: Color = Color.Black,
    trackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .clip(RoundedCornerShape(10.dp)),
        color = progressColor,
        trackColor = trackColor
    )
}

@Composable
fun BudgetLineChart(
    aggregatedData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    if (aggregatedData.isEmpty()) {
        Text("No data to display", modifier = modifier)
        return
    }


    val sortedData = aggregatedData.mapNotNull { (dateStr, amount) ->
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE) to amount
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }.sortedBy { it.first }

    if (sortedData.isEmpty()) {
        Text("No valid data", modifier = modifier)
        return
    }


    val fixedSpacingDp = 120.dp

    val chartWidthDp = if (sortedData.size > 1) {
        fixedSpacingDp * (sortedData.size - 1) + 16.dp
    } else {
        fixedSpacingDp
    }


    val maxAmount = sortedData.maxOf { it.second }


    val yTickCount = 5
    val yTickValues = (0..yTickCount).map { it * maxAmount / yTickCount }


    Row(modifier = modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            yTickValues.reversed().forEach { tick ->
                Text(
                    text = tick.toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
            }
        }


        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ) {

            Canvas(modifier = Modifier
                .width(chartWidthDp)
                .height(160.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height


                val points = sortedData.mapIndexed { index, (date, amount) ->
                    val x = index * fixedSpacingDp.toPx()
                    val y = canvasHeight - (amount / maxAmount * canvasHeight)
                    Offset(x.toFloat(), y.toFloat())
                }


                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = Color.Blue,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 4f
                    )
                }

                points.forEach { point ->
                    drawCircle(
                        color = Color.Red,
                        radius = 6f,
                        center = point
                    )
                }
            }


            Row(
                modifier = Modifier
                    .width(chartWidthDp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sortedData.forEach { (date, _) ->
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("MM/dd")),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}