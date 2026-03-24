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
import com.wealthos.common.SpendingPeriodViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPeriodScreen(
    viewModel: SpendingPeriodViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    
    val amounts = remember { mutableStateMapOf<String, String>() }

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
                            val period = SpendingPeriod(
                                name = name,
                                startDate = LocalDate.parse(startDate),
                                endDate = LocalDate.parse(endDate),
                                createdAt = Clock.System.now(),
                                salary = amounts["salary"]?.toDoubleOrNull() ?: 0.0,
                                otherIncome = amounts["otherIncome"]?.toDoubleOrNull() ?: 0.0,
                                partnerContributions = amounts["partnerContributions"]?.toDoubleOrNull() ?: 0.0,
                                mortgage = amounts["mortgage"]?.toDoubleOrNull() ?: 0.0,
                                bills = amounts["bills"]?.toDoubleOrNull() ?: 0.0,
                                groceries = amounts["groceries"]?.toDoubleOrNull() ?: 0.0,
                                transport = amounts["transport"]?.toDoubleOrNull() ?: 0.0,
                                personalCare = amounts["personalCare"]?.toDoubleOrNull() ?: 0.0,
                                dentist = amounts["dentist"]?.toDoubleOrNull() ?: 0.0,
                                expenses = amounts["expenses"]?.toDoubleOrNull() ?: 0.0,
                                eatingOut = amounts["eatingOut"]?.toDoubleOrNull() ?: 0.0,
                                shopping = amounts["shopping"]?.toDoubleOrNull() ?: 0.0,
                                entertainment = amounts["entertainment"]?.toDoubleOrNull() ?: 0.0,
                                books = amounts["books"]?.toDoubleOrNull() ?: 0.0,
                                clothing = amounts["clothing"]?.toDoubleOrNull() ?: 0.0,
                                gifts = amounts["gifts"]?.toDoubleOrNull() ?: 0.0,
                                tech = amounts["tech"]?.toDoubleOrNull() ?: 0.0,
                                drinks = amounts["drinks"]?.toDoubleOrNull() ?: 0.0,
                                holidays = amounts["holidays"]?.toDoubleOrNull() ?: 0.0,
                                lego = amounts["lego"]?.toDoubleOrNull() ?: 0.0,
                                gaming = amounts["gaming"]?.toDoubleOrNull() ?: 0.0,
                                comics = amounts["comics"]?.toDoubleOrNull() ?: 0.0,
                                psychotherapy = amounts["psychotherapy"]?.toDoubleOrNull() ?: 0.0,
                                gym = amounts["gym"]?.toDoubleOrNull() ?: 0.0,
                                cycling = amounts["cycling"]?.toDoubleOrNull() ?: 0.0,
                                culture = amounts["culture"]?.toDoubleOrNull() ?: 0.0,
                                parents = amounts["parents"]?.toDoubleOrNull() ?: 0.0,
                                savings = amounts["savings"]?.toDoubleOrNull() ?: 0.0,
                                investment = amounts["investment"]?.toDoubleOrNull() ?: 0.0,
                                sipp = amounts["sipp"]?.toDoubleOrNull() ?: 0.0
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
            
            SectionHeader("Incomes")
            AmountField("Salary", "salary", amounts)
            AmountField("Other Income", "otherIncome", amounts)
            AmountField("Partner Contributions", "partnerContributions", amounts)

            SectionHeader("Needs")
            AmountField("Mortgage", "mortgage", amounts)
            AmountField("Bills", "bills", amounts)
            AmountField("Groceries", "groceries", amounts)
            AmountField("Transport", "transport", amounts)
            AmountField("Personal Care", "personalCare", amounts)
            AmountField("Dentist", "dentist", amounts)
            AmountField("Expenses", "expenses", amounts)

            SectionHeader("Wants")
            AmountField("Eating Out", "eatingOut", amounts)
            AmountField("Shopping", "shopping", amounts)
            AmountField("Entertainment", "entertainment", amounts)
            AmountField("Books", "books", amounts)
            AmountField("Clothing", "clothing", amounts)
            AmountField("Gifts", "gifts", amounts)
            AmountField("Tech", "tech", amounts)
            AmountField("Drinks", "drinks", amounts)
            AmountField("Holidays", "holidays", amounts)
            AmountField("Lego", "lego", amounts)
            AmountField("Gaming", "gaming", amounts)
            AmountField("Comics", "comics", amounts)
            AmountField("Psychotherapy", "psychotherapy", amounts)
            AmountField("Gym", "gym", amounts)
            AmountField("Cycling", "cycling", amounts)
            AmountField("Culture", "culture", amounts)
            AmountField("Parents", "parents", amounts)

            SectionHeader("Savings")
            AmountField("Savings", "savings", amounts)
            AmountField("Investment", "investment", amounts)
            AmountField("SIPP", "sipp", amounts)
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
fun AmountField(label: String, key: String, amounts: MutableMap<String, String>) {
    OutlinedTextField(
        value = amounts[key] ?: "",
        onValueChange = { amounts[key] = it },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        prefix = { Text("£") }
    )
}
