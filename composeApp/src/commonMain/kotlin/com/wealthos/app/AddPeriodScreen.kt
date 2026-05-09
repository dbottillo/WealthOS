package com.wealthos.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wealthos.common.SpendingPeriod
import com.wealthos.common.SpendingEntry
import com.wealthos.common.SpendingPeriodViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPeriodScreen(
    viewModel: SpendingPeriodViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    
    val amounts = remember { mutableStateMapOf<Int, String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Period") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(onClick = {
                        try {
                            val entries = uiState.categories.map { category ->
                                SpendingEntry(
                                    categoryName = category.name,
                                    amount = amounts[category.id]?.toDoubleOrNull() ?: 0.0,
                                    bucket = category.bucket
                                )
                            }
                            val period = SpendingPeriod(
                                name = name,
                                startDate = LocalDate.parse(startDate),
                                endDate = LocalDate.parse(endDate),
                                createdAt = Clock.System.now(),
                                entries = entries
                            )
                            viewModel.savePeriod(period)
                            onBack()
                        } catch (e: Exception) {
                            // Validation error or parse error
                        }
                    }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name (e.g. Jan 2026)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date (YYYY-MM-DD)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date (YYYY-MM-DD)") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            val groupedCategories = uiState.categories.groupBy { it.bucket }
            val bucketOrder = listOf("INCOME", "NEED", "WANT", "SAVING", "UNCATEGORIZED")

            bucketOrder.forEach { bucket ->
                val categoriesInBucket = groupedCategories[bucket]
                if (!categoriesInBucket.isNullOrEmpty()) {
                    SectionHeader(bucket)
                    categoriesInBucket.forEach { category ->
                        AmountField(category.name, category.id, amounts)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun AmountField(label: String, categoryId: Int, amounts: MutableMap<Int, String>) {
    OutlinedTextField(
        value = amounts[categoryId] ?: "",
        onValueChange = { amounts[categoryId] = it },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        prefix = { Text("£") }
    )
}
