package com.wealthos.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.wealthos.common.SpendingPeriodDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsDashboard(periods: List<SpendingPeriodDto>) {
    if (periods.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No data available for analytics.")
        }
        return
    }

    var selectedTimeRange by remember { mutableStateOf(TimeRange.SIX) }

    // Server returns periods sorted by startDate DESC (newest first)
    val latestPeriod = periods.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Latest Period: ${latestPeriod.name}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        SummaryCards(latestPeriod)
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("50/30/20 Distribution", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        BucketDistributionChart(latestPeriod)

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Spending Trend", style = MaterialTheme.typography.titleLarge)
            SingleChoiceSegmentedButtonRow {
                TimeRange.entries.forEachIndexed { index, range ->
                    SegmentedButton(
                        selected = selectedTimeRange == range,
                        onClick = { selectedTimeRange = range },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = TimeRange.entries.size),
                        label = { Text(range.label) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Take the selected number of periods and reverse them to show chronological order
        SpendingTrendChart(periods.take(selectedTimeRange.months).reversed())
    }
}

@Composable
fun SummaryCards(period: SpendingPeriodDto) {
    val isDark = isSystemInDarkTheme()
    val balanceColor = if (isDark) {
        if (period.balance >= 0) Color(0xFF81C784) else Color(0xFFE57373)
    } else {
        if (period.balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoCard("Income", "£${period.totalIncome.toInt()}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        InfoCard("Spending", "£${period.totalSpending.toInt()}", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
        InfoCard("Balance", "£${period.balance.toInt()}", balanceColor, Modifier.weight(1f))
    }
}

@Composable
fun InfoCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleMedium, color = color)
        }
    }
}

@Composable
fun BucketDistributionChart(period: SpendingPeriodDto) {
    val needs = period.totalNeeds.toFloat()
    val wants = period.totalWants.toFloat()
    val savings = period.totalSavings.toFloat()
    val total = needs + wants + savings

    if (total == 0f) return

    val needsAngle = (needs / total) * 360f
    val wantsAngle = (wants / total) * 360f
    val savingsAngle = (savings / total) * 360f

    val isDark = isSystemInDarkTheme()
    val needsColor = if (isDark) Color(0xFF90CAF9) else Color(0xFF2196F3)
    val wantsColor = if (isDark) Color(0xFFFFE082) else Color(0xFFFFC107)
    val savingsColor = if (isDark) Color(0xFF81C784) else Color(0xFF4CAF50)

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(150.dp)) {
            drawArc(needsColor, 0f, needsAngle, true)
            drawArc(wantsColor, needsAngle, wantsAngle, true)
            drawArc(savingsColor, needsAngle + wantsAngle, savingsAngle, true)
        }
        
        Spacer(modifier = Modifier.width(24.dp))
        
        Column {
            LegendItem("Needs (50%)", needsColor, "${(period.needsPercentage * 100).toInt()}%")
            LegendItem("Wants (30%)", wantsColor, "${(period.wantsPercentage * 100).toInt()}%")
            LegendItem("Savings (20%)", savingsColor, "${(period.savingsPercentage * 100).toInt()}%")
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color, percentage: String) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Surface(modifier = Modifier.size(12.dp), color = color, shape = MaterialTheme.shapes.small) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label: $percentage", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SpendingTrendChart(periods: List<SpendingPeriodDto>) {
    val maxSpending = periods.maxOfOrNull { it.totalSpending }?.toFloat() ?: 1f
    val lineColor = MaterialTheme.colorScheme.primary
    
    Canvas(modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 16.dp)) {
        val width = size.width
        val height = size.height
        val spacing = width / (periods.size - 1).coerceAtLeast(1)

        val points = periods.mapIndexed { index, period ->
            val x = index * spacing
            val y = height - (period.totalSpending.toFloat() / maxSpending * height)
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f
            )
        }
        
        points.forEach { point ->
            drawCircle(lineColor, radius = 6f, center = point)
        }
    }
}
