package com.wealthos.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthos.common.SpendingPeriodDto
import com.wealthos.common.SpendingPeriodViewModel
import org.koin.compose.viewmodel.koinViewModel

enum class Screen {
    List, Add, Analytics
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var currentScreen by remember { mutableStateOf(Screen.List) }
    val viewModel: SpendingPeriodViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEADDFF),
            secondary = Color(0xFF625B71),
            background = Color(0xFFF6F6F6)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.List -> PeriodListScreen(
                    viewModel = viewModel,
                    onNavigateToAdd = { currentScreen = Screen.Add },
                    onNavigateToAnalytics = { currentScreen = Screen.Analytics }
                )
                Screen.Add -> AddPeriodScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = Screen.List }
                )
                Screen.Analytics -> Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Analytics") },
                            navigationIcon = {
                                IconButton(onClick = { currentScreen = Screen.List }) {
                                    Icon(Icons.Default.List, contentDescription = "List")
                                }
                            }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        AnalyticsDashboard(state.periods)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodListScreen(
    viewModel: SpendingPeriodViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedPeriod by remember { mutableStateOf<SpendingPeriodDto?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("WealthOS", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToAnalytics) {
                            Icon(Icons.Default.Settings, contentDescription = "Analytics")
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToAdd,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Entry") }
                )
            }
        ) { padding ->
            Row(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // Left Column: 6-month Average Pie Chart & Actions
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 32.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        AveragePieChartSection(state.periods)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TextButton(
                        onClick = { viewModel.triggerMigration() },
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync Notion", style = MaterialTheme.typography.labelLarge)
                    }
                }

                // Right Column: Scrollable List
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .padding(end = 16.dp)
                ) {
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

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.periods, key = { it.id ?: (it.name + it.startDate.toString()) }) { period ->
                            PeriodRow(period, onClick = { 
                                selectedPeriod = period
                            })
                        }
                    }
                }
            }
        }

        // Overlay to capture clicks outside the panel
        if (selectedPeriod != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
                    .clickable { selectedPeriod = null }
            )
        }

        // Side Detail Panel
        AnimatedVisibility(
            visible = selectedPeriod != null,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(450.dp)
        ) {
            selectedPeriod?.let { period ->
                PeriodDetailPanel(period, onClose = { selectedPeriod = null })
            }
        }
    }
}

@Composable
fun PeriodDetailPanel(period: SpendingPeriodDto, onClose: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Escape && keyEvent.type == KeyEventType.KeyUp) {
                    onClose()
                    true
                } else false
            }
            .clickable(enabled = false) {},
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        shape = androidx.compose.ui.graphics.RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(period.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Text("${period.startDate} to ${period.endDate}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
            
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                DetailSection("Summary", listOf(
                    "Total Income" to "£${period.totalIncome.toInt()}",
                    "Total Spending" to "£${period.totalSpending.toInt()}",
                    "Balance" to "£${period.balance.toInt()}"
                ))

                DetailSection("Buckets (Target 50/30/20)", listOf(
                    "Needs" to "£${period.totalNeeds.toInt()} (${(period.needsPercentage * 100).toInt()}%)",
                    "Wants" to "£${period.totalWants.toInt()} (${(period.wantsPercentage * 100).toInt()}%)",
                    "Savings" to "£${period.totalSavings.toInt()} (${(period.savingsPercentage * 100).toInt()}%)"
                ))

                DetailSection("Income Details", listOf(
                    "Salary" to "£${period.salary.toInt()}",
                    "Other Income" to "£${period.otherIncome.toInt()}",
                    "Partner Contributions" to "£${period.partnerContributions.toInt()}"
                ))

                DetailSection("Needs Breakdown", listOf(
                    "Mortgage" to "£${period.mortgage.toInt()}",
                    "Bills" to "£${period.bills.toInt()}",
                    "Groceries" to "£${period.groceries.toInt()}",
                    "Transport" to "£${period.transport.toInt()}",
                    "Personal Care" to "£${period.personalCare.toInt()}",
                    "Dentist" to "£${period.dentist.toInt()}",
                    "Expenses" to "£${period.expenses.toInt()}"
                ))

                DetailSection("Wants Breakdown", listOf(
                    "Eating Out" to "£${period.eatingOut.toInt()}",
                    "Shopping" to "£${period.shopping.toInt()}",
                    "Entertainment" to "£${period.entertainment.toInt()}",
                    "Books" to "£${period.books.toInt()}",
                    "Clothing" to "£${period.clothing.toInt()}",
                    "Gifts" to "£${period.gifts.toInt()}",
                    "Tech" to "£${period.tech.toInt()}",
                    "Drinks" to "£${period.drinks.toInt()}",
                    "Holidays" to "£${period.holidays.toInt()}",
                    "Lego" to "£${period.lego.toInt()}",
                    "Gaming" to "£${period.gaming.toInt()}",
                    "Comics" to "£${period.comics.toInt()}",
                    "Psychotherapy" to "£${period.psychotherapy.toInt()}",
                    "Gym" to "£${period.gym.toInt()}",
                    "Cycling" to "£${period.cycling.toInt()}",
                    "Culture" to "£${period.culture.toInt()}",
                    "Parents" to "£${period.parents.toInt()}"
                ))

                DetailSection("Savings & Investments", listOf(
                    "Direct Savings" to "£${period.savings.toInt()}",
                    "ISA/Investments" to "£${period.investment.toInt()}",
                    "SIPP (Pension)" to "£${period.sipp.toInt()}"
                ))

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DetailSection(title: String, items: List<Pair<String, String>>) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        items.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, style = MaterialTheme.typography.bodyMedium)
                Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
    }
}

@Composable
fun AveragePieChartSection(periods: List<SpendingPeriodDto>) {
    val lastSixMonths = periods.takeLast(6)
    if (lastSixMonths.isEmpty()) return

    val avgNeeds = lastSixMonths.map { it.needsPercentage }.average()
    val avgWants = lastSixMonths.map { it.wantsPercentage }.average()
    val avgSavings = lastSixMonths.map { it.savingsPercentage }.average()

    val dummyDto = lastSixMonths.last().copy(
        needsPercentage = avgNeeds,
        wantsPercentage = avgWants,
        savingsPercentage = avgSavings,
        totalNeeds = avgNeeds,
        totalWants = avgWants,
        totalSavings = avgSavings
    )

    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "6-Month Average",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        BucketDistributionChart(dummyDto)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PeriodRow(period: SpendingPeriodDto, onClick: () -> Unit) {
    val purple = Color(0xFFF3E5F5)
    val blue = Color(0xFFE3F2FD)
    val yellow = Color(0xFFFFFDE7)
    val green = Color(0xFFE8F5E9)
    val red = Color(0xFFFFEBEE)
    
    val statusColor = when(period.status) {
        "CRITICAL" -> Color(0xFFF44336)
        "WARNING" -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }
    
    val statusIcon = when(period.status) {
        "CRITICAL" -> Icons.Default.Close
        "WARNING" -> Icons.Default.Warning
        else -> Icons.Default.Check
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(statusColor)
            )
            
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(period.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${period.startDate} - ${period.endDate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 8.dp)) {
                            Text(
                                text = "£${period.balance.toInt()}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = period.status,
                            tint = statusColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip("Salary", "£${period.salary.toInt()}", purple)
                    InfoChip("In", "£${period.totalIncome.toInt()}", purple)
                    InfoChip("Out", "£${period.totalSpending.toInt()}", purple)
                    InfoChip("Spending", "£${(period.totalNeeds + period.totalWants).toInt()}", purple)
                    
                    InfoChip("Needs", "£${period.totalNeeds.toInt()} (${(period.needsPercentage * 100).toInt()}%)", blue)
                    InfoChip("Wants", "£${period.totalWants.toInt()} (${(period.wantsPercentage * 100).toInt()}%)", yellow)
                    InfoChip("Save", "£${period.totalSavings.toInt()} (${(period.savingsPercentage * 100).toInt()}%)", green)
                }
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String, bgColor: Color) {
    Surface(
        color = bgColor,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("$label: ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Text(value, style = MaterialTheme.typography.labelSmall, color = Color.Black)
        }
    }
}
