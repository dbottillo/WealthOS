package com.wealthos.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthos.common.SpendingPeriodDto
import com.wealthos.common.SpendingPeriodViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

enum class Screen {
    List, Add, Analytics
}

data class ColumnDef(val title: String, val width: androidx.compose.ui.unit.Dp)

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
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Detailed")

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
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.isLoading && selectedTabIndex == 0) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                
                if (state.error != null) {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                when (selectedTabIndex) {
                    0 -> {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 16.dp, horizontal = 16.dp),
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

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    TrendsSection(state.periods)
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

                            LazyColumn(
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxHeight(),
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
                    1 -> {
                        DetailedTableView(state.periods, onRowClick = { selectedPeriod = it })
                    }
                }
            }
        }

        if (selectedPeriod != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.1f))
                    .clickable { selectedPeriod = null }
            )
        }

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
fun TrendsSection(periods: List<SpendingPeriodDto>) {
    // periods is sorted by date DESC (newest first)
    if (periods.size < 6) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("6-Month Trends", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Not enough data for 6-month trends.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        return
    }

    val currentThree = periods.take(3) // Newest 3
    val previousThree = periods.drop(3).take(3) // 3 before that

    Column(modifier = Modifier.padding(24.dp)) {
        Text("6-Month Trends", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        TrendRow("Total Spending", currentThree.map { it.totalSpending }.average(), previousThree.map { it.totalSpending }.average(), invert = true)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Needs", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        TrendRow("Mortgage", currentThree.map { it.mortgage }.average(), previousThree.map { it.mortgage }.average(), invert = true)
        TrendRow("Bills", currentThree.map { it.bills }.average(), previousThree.map { it.bills }.average(), invert = true)
        TrendRow("Groceries", currentThree.map { it.groceries }.average(), previousThree.map { it.groceries }.average(), invert = true)
        TrendRow("Transport", currentThree.map { it.transport }.average(), previousThree.map { it.transport }.average(), invert = true)
        TrendRow("Personal Care", currentThree.map { it.personalCare }.average(), previousThree.map { it.personalCare }.average(), invert = true)
        TrendRow("Dentist", currentThree.map { it.dentist }.average(), previousThree.map { it.dentist }.average(), invert = true)
        TrendRow("Expenses", currentThree.map { it.expenses }.average(), previousThree.map { it.expenses }.average(), invert = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Wants", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        TrendRow("Eating Out", currentThree.map { it.eatingOut }.average(), previousThree.map { it.eatingOut }.average(), invert = true)
        TrendRow("Shopping", currentThree.map { it.shopping }.average(), previousThree.map { it.shopping }.average(), invert = true)
        TrendRow("Entertainment", currentThree.map { it.entertainment }.average(), previousThree.map { it.entertainment }.average(), invert = true)
        TrendRow("Books", currentThree.map { it.books }.average(), previousThree.map { it.books }.average(), invert = true)
        TrendRow("Clothing", currentThree.map { it.clothing }.average(), previousThree.map { it.clothing }.average(), invert = true)
        TrendRow("Gifts", currentThree.map { it.gifts }.average(), previousThree.map { it.gifts }.average(), invert = true)
        TrendRow("Tech", currentThree.map { it.tech }.average(), previousThree.map { it.tech }.average(), invert = true)
        TrendRow("Drinks", currentThree.map { it.drinks }.average(), previousThree.map { it.drinks }.average(), invert = true)
        TrendRow("Holidays", currentThree.map { it.holidays }.average(), previousThree.map { it.holidays }.average(), invert = true)
        TrendRow("Lego", currentThree.map { it.lego }.average(), previousThree.map { it.lego }.average(), invert = true)
        TrendRow("Gaming", currentThree.map { it.gaming }.average(), previousThree.map { it.gaming }.average(), invert = true)
        TrendRow("Comics", currentThree.map { it.comics }.average(), previousThree.map { it.comics }.average(), invert = true)
        TrendRow("Psychotherapy", currentThree.map { it.psychotherapy }.average(), previousThree.map { it.psychotherapy }.average(), invert = true)
        TrendRow("Gym", currentThree.map { it.gym }.average(), previousThree.map { it.gym }.average(), invert = true)
        TrendRow("Cycling", currentThree.map { it.cycling }.average(), previousThree.map { it.cycling }.average(), invert = true)
        TrendRow("Culture", currentThree.map { it.culture }.average(), previousThree.map { it.culture }.average(), invert = true)
        TrendRow("Parents", currentThree.map { it.parents }.average(), previousThree.map { it.parents }.average(), invert = true)

        Spacer(modifier = Modifier.height(16.dp))
        Text("Savings & Investment", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        TrendRow("ISA/Investments", currentThree.map { it.investment }.average(), previousThree.map { it.investment }.average())
        TrendRow("SIPP", currentThree.map { it.sipp }.average(), previousThree.map { it.sipp }.average())
    }
}

@Composable
fun TrendRow(label: String, current: Double, previous: Double, invert: Boolean = false, isPercentage: Boolean = false) {
    val diff = current - previous
    val threshold = 1.0 // Minimal change threshold for £
    
    val trendData: Pair<ImageVector, Color> = when {
        abs(diff) < threshold -> Pair(Icons.Default.Menu, Color.Gray)
        diff > 0 -> Pair(Icons.Default.KeyboardArrowUp, if (invert) Color(0xFFC62828) else Color(0xFF2E7D32))
        else -> Pair(Icons.Default.KeyboardArrowDown, if (invert) Color(0xFF2E7D32) else Color(0xFFC62828))
    }
    val icon = trendData.first
    val color = trendData.second
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val unit = if (isPercentage) "%" else ""
            val prefix = if (!isPercentage) "£" else ""
            Text(
                text = "$prefix${current.toInt()}$unit",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 4.dp)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun DetailedTableView(periods: List<SpendingPeriodDto>, onRowClick: (SpendingPeriodDto) -> Unit) {
    val periodsByYear = periods.groupBy { it.endDate.year }
    val sortedYears = periodsByYear.keys.sortedDescending()
    val horizontalScrollState = rememberScrollState()

    val stickyColumnWidth = 140.dp
    val columnWidth = 90.dp
    
    val scrollableColumns = listOf(
        ColumnDef("Money In", columnWidth),
        ColumnDef("Money Out", columnWidth),
        ColumnDef("Balance", columnWidth),
        ColumnDef("Investments", columnWidth),
        ColumnDef("SIPP", columnWidth),
        ColumnDef("Spending", columnWidth),
        ColumnDef("Mortgage", columnWidth),
        ColumnDef("Bills", columnWidth),
        ColumnDef("Groceries", columnWidth),
        ColumnDef("Eating Out", columnWidth),
        ColumnDef("Shopping", columnWidth),
        ColumnDef("Entertain.", columnWidth),
        ColumnDef("Books", columnWidth),
        ColumnDef("Clothing", columnWidth),
        ColumnDef("Gifts", columnWidth),
        ColumnDef("Tech", columnWidth),
        ColumnDef("Drinks", columnWidth),
        ColumnDef("Holidays", columnWidth),
        ColumnDef("Lego", columnWidth),
        ColumnDef("Gaming", columnWidth),
        ColumnDef("Comics", columnWidth),
        ColumnDef("Psychotherapy", columnWidth),
        ColumnDef("Gym", columnWidth),
        ColumnDef("Cycling", columnWidth),
        ColumnDef("Culture", columnWidth),
        ColumnDef("Parents", columnWidth),
        ColumnDef("Personal Care", columnWidth),
        ColumnDef("Dentist", columnWidth),
        ColumnDef("Expenses", columnWidth),
        ColumnDef("% Needs", 80.dp),
        ColumnDef("% Wants", 80.dp),
        ColumnDef("% Savings", 80.dp)
    )

    val dividerColor = Color.LightGray.copy(alpha = 0.4f)
    val purpleHeader = Color(0xFFF3E5F5)
    val purpleCell = Color(0xFFFBF7FD)
    val yellowHeader = Color(0xFFFFF9C4)
    val yellowCell = Color(0xFFFFFDE7)
    val blueHeader = Color(0xFFE3F2FD)
    val blueCell = Color(0xFFF5F9FF)
    val greenHeader = Color(0xFFE8F5E9)
    val greenCell = Color(0xFFF1F8F1)

    Column(modifier = Modifier.fillMaxSize().background(Color.White).border(1.dp, dividerColor)) {
        Row(modifier = Modifier.height(IntrinsicSize.Min).background(MaterialTheme.colorScheme.primaryContainer)) {
            TableCell(text = "Period", width = stickyColumnWidth, isHeader = true, bgColor = Color.White)
            VerticalDivider(color = dividerColor)
            
            Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                scrollableColumns.forEach { col ->
                    val bgColor = when (col.title) {
                        "Money In", "Money Out", "Balance" -> purpleHeader
                        "Investments", "SIPP" -> yellowHeader
                        "% Needs", "% Wants", "% Savings" -> greenHeader
                        else -> blueHeader
                    }
                    TableCell(text = col.title, width = col.width, isHeader = true, bgColor = bgColor)
                    VerticalDivider(color = dividerColor)
                }
            }
        }
        
        HorizontalDivider(color = dividerColor)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            sortedYears.forEach { year ->
                val yearPeriods = periodsByYear[year] ?: emptyList()
                
                item(key = "header_$year") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                    ) {
                        Text(year.toString(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold)
                    }
                    HorizontalDivider(color = dividerColor)
                }

                items(yearPeriods, key = { it.id ?: (it.name + it.startDate.toString()) }) { period ->
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .clickable { onRowClick(period) }
                    ) {
                        TableCell(text = period.name, width = stickyColumnWidth, fontWeight = FontWeight.Bold, bgColor = Color.White)
                        VerticalDivider(color = dividerColor)

                        Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                            scrollableColumns.forEach { col ->
                                val value = getCellValue(period, col.title)
                                val textColor = if (col.title == "Balance") {
                                    if (period.balance >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                } else Color.Unspecified
                                
                                val bgColor = when (col.title) {
                                    "Money In", "Money Out", "Balance" -> purpleCell
                                    "Investments", "SIPP" -> yellowCell
                                    "% Needs", "% Wants", "% Savings" -> greenCell
                                    else -> blueCell
                                }
                                
                                TableCell(text = value, width = col.width, color = textColor, bgColor = bgColor)
                                VerticalDivider(color = dividerColor)
                            }
                        }
                    }
                    HorizontalDivider(color = dividerColor)
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    bgColor: Color = Color.Transparent
) {
    Box(modifier = Modifier.width(width).background(bgColor)) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            style = if (isHeader) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            fontWeight = fontWeight ?: if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (isHeader) TextAlign.Start else TextAlign.End
        )
    }
}

private fun getCellValue(period: SpendingPeriodDto, title: String): String {
    return when (title) {
        "Money In" -> "£${period.totalIncome.toInt()}"
        "Money Out" -> "£${period.totalSpending.toInt()}"
        "Balance" -> "£${period.balance.toInt()}"
        "Investments" -> "£${period.investment.toInt()}"
        "SIPP" -> "£${period.sipp.toInt()}"
        "Spending" -> "£${(period.totalNeeds + period.totalWants).toInt()}"
        "Mortgage" -> "£${period.mortgage.toInt()}"
        "Bills" -> "£${period.bills.toInt()}"
        "Groceries" -> "£${period.groceries.toInt()}"
        "Eating Out" -> "£${period.eatingOut.toInt()}"
        "Shopping" -> "£${period.shopping.toInt()}"
        "Entertain." -> "£${period.entertainment.toInt()}"
        "Books" -> "£${period.books.toInt()}"
        "Clothing" -> "£${period.clothing.toInt()}"
        "Gifts" -> "£${period.gifts.toInt()}"
        "Tech" -> "£${period.tech.toInt()}"
        "Drinks" -> "£${period.drinks.toInt()}"
        "Holidays" -> "£${period.holidays.toInt()}"
        "Lego" -> "£${period.lego.toInt()}"
        "Gaming" -> "£${period.gaming.toInt()}"
        "Comics" -> "£${period.comics.toInt()}"
        "Psychotherapy" -> "£${period.psychotherapy.toInt()}"
        "Gym" -> "£${period.gym.toInt()}"
        "Cycling" -> "£${period.cycling.toInt()}"
        "Culture" -> "£${period.culture.toInt()}"
        "Parents" -> "£${period.parents.toInt()}"
        "Personal Care" -> "£${period.personalCare.toInt()}"
        "Dentist" -> "£${period.dentist.toInt()}"
        "Expenses" -> "£${period.expenses.toInt()}"
        "% Needs" -> "${(period.needsPercentage * 100).toInt()}%"
        "% Wants" -> "${(period.wantsPercentage * 100).toInt()}%"
        "% Savings" -> "${(period.savingsPercentage * 100).toInt()}%"
        else -> ""
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
