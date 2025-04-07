package com.example.ass2_travaler.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.material.icons.filled.Star

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton


import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import com.example.ass2_travaler.componentTool.CurrencyConverter
import com.example.ass2_travaler.componentTool.toCurrencyString

// screens/CurrencyTransfer.kt
@Composable
fun CurrencyTransfer() {
    var from by remember { mutableStateOf("USD") }
    var to by remember { mutableStateOf("CNY") }
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf(0.0) }
    val converter = remember { CurrencyConverter() }

    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {

        TextField(
            value = amount,
            onValueChange = { input ->
                if (input.matches(Regex("^\\d*\\.?\\d*\$")) || input.isEmpty()) {
                    amount = input
                    result = converter.convert(
                        amount.toDoubleOrNull() ?: 0.0,
                        from,
                        to
                    )
                }
            },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(2f),
//            colors = TextFieldDefaults.textFieldColors(
//                containerColor = Color.Green
//            )
        )


        CurrencyDropdown(
            selected = from,
            onSelected = {
                from = it
                result = converter.convert(
                    amount.toDoubleOrNull() ?: 0.0,
                    from,
                    to
                )
            },
            modifier = Modifier.weight(1f)
        )


        IconButton(
            onClick = {
                val temp = from
                from = to
                to = temp
                result = converter.convert(
                    amount.toDoubleOrNull() ?: 0.0,
                    from,
                    to
                )
            },
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = "Transfer Currency",
                tint = MaterialTheme.colorScheme.primary
            )
        }


        CurrencyDropdown(
            selected = to,
            onSelected = {
                to = it
                result = converter.convert(
                    amount.toDoubleOrNull() ?: 0.0,
                    from,
                    to
                )
            },
            modifier = Modifier.weight(1f)
        )


        Text(
            text = result.toCurrencyString(to),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .weight(2f)
                .padding(start = 8.dp)
        )
    }
}
@Composable
fun CurrencyDropdown(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencies = listOf("USD", "CNY", "EUR", "GBP", "JPY", "SGD")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.size(width = 100.dp, height = 56.dp)
        ) {
            Text(selected)
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "List",
                modifier = Modifier.size(24.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onSelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}