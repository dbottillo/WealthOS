package com.wealthos.app

import androidx.compose.foundation.Canvas
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

@Composable
fun AnalyticsDashboard(periods: List<SpendingPeriodDto>) {
    if (periods.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No data available for analytics.")
        }
        return
    }

    val latestPeriod = periods.last()

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
        Text("Spending Trend (Last 6 Periods)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        SpendingTrendChart(periods.takeLast(6))
    }
}

@Composable
fun SummaryCards(period: SpendingPeriodDto) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoCard("Income", "£${period.totalIncome.toInt()}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
        InfoCard("Spending", "£${period.totalSpending.toInt()}", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
        InfoCard("Balance", "£${period.balance.toInt()}", if (period.balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336), Modifier.weight(1f))
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

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(150.dp)) {
            drawArc(Color(0xFF2196F3), 0f, needsAngle, true)
            drawArc(Color(0xFFFFC107), needsAngle, wantsAngle, true)
            drawArc(Color(0xFF4CAF50), needsAngle + wantsAngle, savingsAngle, true)
        }
        
        Spacer(modifier = Modifier.width(24.dp))
        
        Column {
            LegendItem("Needs (50%)", Color(0xFF2196F3), "${(period.needsPercentage * 100).toInt()}%")
            LegendItem("Wants (30%)", Color(0xFFFFC107), "${(period.wantsPercentage * 100).toInt()}%")
            LegendItem("Savings (20%)", Color(0xFF4CAF50), "${(period.savingsPercentage * 100).toInt()}%")
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
                color = Color(0xFF6200EE),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f
            )
        }
        
        points.forEach { point ->
            drawCircle(Color(0xFF6200EE), radius = 6f, center = point)
        }
    }
}
