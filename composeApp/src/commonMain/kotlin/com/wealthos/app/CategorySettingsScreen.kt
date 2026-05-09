package com.wealthos.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wealthos.common.CategoryDto
import com.wealthos.common.SpendingPeriodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySettingsScreen(
    viewModel: SpendingPeriodViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Category Manager") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.categories.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No categories found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                items(uiState.categories) { category ->
                    CategoryItem(category) { newBucket ->
                        viewModel.updateCategoryBucket(category.id, newBucket)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: CategoryDto, onUpdateBucket: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val buckets = listOf("INCOME", "NEED", "WANT", "SAVING", "UNCATEGORIZED")

    ListItem(
        modifier = Modifier.clickable { expanded = true },
        headlineContent = { Text(category.name) },
        supportingContent = { 
            Text(
                text = "Bucket: ${category.bucket}",
                color = if (category.bucket == "UNCATEGORIZED") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            ) 
        },
        trailingContent = {
            Box {
                Icon(
                    imageVector = if (category.bucket == "UNCATEGORIZED") Icons.Default.Info else Icons.Default.Edit, 
                    contentDescription = null,
                    tint = if (category.bucket == "UNCATEGORIZED") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    buckets.forEach { bucket ->
                        DropdownMenuItem(
                            text = { Text(bucket) },
                            onClick = {
                                onUpdateBucket(bucket)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}
