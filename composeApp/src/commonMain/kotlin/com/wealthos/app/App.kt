package com.wealthos.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wealthos.common.SpendingPeriodDto
import com.wealthos.common.SpendingPeriodViewModel
import com.hoc081098.kmp.viewmodel.koin.compose.koinKmpViewModel

enum class Screen {
    List, Add
}

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.List) }
    val viewModel: SpendingPeriodViewModel = koinKmpViewModel()

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.List -> PeriodListScreen(
                    viewModel = viewModel,
                    onNavigateToAdd = { currentScreen = Screen.Add }
                )
                Screen.Add -> AddPeriodScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = Screen.List }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodListScreen(
    viewModel: SpendingPeriodViewModel,
    onNavigateToAdd: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("WealthOS") },
                actions = {
                    IconButton(onClick = { viewModel.triggerMigration() }) {
                        Text("Migrate", style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Period")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            if (state.error != null) {
                Text(
                    text = "Error: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.periods) { period ->
                    PeriodRow(period)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun PeriodRow(period: SpendingPeriodDto) {
    ListItem(
        headlineContent = { Text(period.name) },
        supportingContent = {
            Text("${period.startDate} - ${period.endDate}")
        },
        trailingContent = {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                Text(
                    text = "£${period.balance.toInt()}",
                    color = if (period.balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = period.status,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    )
}
