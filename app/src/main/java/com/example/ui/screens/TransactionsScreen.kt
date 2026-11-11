package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryConstants
import com.example.data.model.TransactionEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.IncomeGreen
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    language: AppLanguage,
    transactions: List<TransactionEntity>,
    searchQuery: String,
    filterType: String,
    filterTimeRange: String,
    onSearchChange: (String) -> Unit,
    onFilterTypeChange: (String) -> Unit,
    onFilterTimeRangeChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit
) {
    var selectedTxForAction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text(AppStrings.searchPlaceholder(language), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GreenPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips (Type)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val types = listOf(
                    "ALL" to AppStrings.all(language),
                    "EXPENSE" to AppStrings.expense(language),
                    "INCOME" to AppStrings.income(language)
                )
                items(types) { (code, label) ->
                    val isSelected = filterType == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterTypeChange(code) },
                        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = GreenPrimary
                        ),
                        modifier = Modifier.testTag("filter_type_$code")
                    )
                }

                item {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Time Range Filters
                val times = listOf(
                    "ALL" to AppStrings.all(language),
                    "TODAY" to AppStrings.today(language),
                    "WEEK" to AppStrings.thisWeek(language),
                    "MONTH" to AppStrings.thisMonth(language)
                )
                items(times) { (code, label) ->
                    val isSelected = filterTimeRange == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterTimeRangeChange(code) },
                        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("filter_time_$code")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total in filtered list indicator
            val totalInList = transactions.sumOf { if (it.type == "INCOME") it.amount else -it.amount }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${transactions.size} ${if (language == AppLanguage.HINDI) "लेन-देन दर्ज" else "transactions"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${if (language == AppLanguage.HINDI) "कुल असर" else "Net"}: ${CurrencyFormatter.formatRupee(totalInList)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (totalInList >= 0) IncomeGreen else ExpenseRed
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List of Transactions
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (language == AppLanguage.HINDI) "कोई लेन-देन नहीं मिला" else "No matching transactions found",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions) { tx ->
                        TransactionRowItem(
                            transaction = tx,
                            language = language,
                            onClick = { selectedTxForAction = tx }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Floating Action Button to add transaction
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = GreenPrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 20.dp)
                .testTag("fab_add_transaction")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Transaction", modifier = Modifier.size(28.dp))
        }
    }

    // Modal Bottom Sheet on clicking an item: View, Edit, or Delete
    selectedTxForAction?.let { tx ->
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { selectedTxForAction = null },
            sheetState = sheetState
        ) {
            val isExpense = tx.type == "EXPENSE"
            val category = CategoryConstants.getCategory(tx.category, isExpense)
            val catName = if (language == AppLanguage.HINDI) category.nameHi else category.nameEn
            val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm a", if (language == AppLanguage.HINDI) Locale("hi", "IN") else Locale.ENGLISH)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(category.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(category.icon, contentDescription = null, tint = category.color, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = catName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = if (isExpense) AppStrings.expense(language) else AppStrings.income(language),
                            color = if (isExpense) ExpenseRed else IncomeGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = CurrencyFormatter.formatRupee(tx.amount),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isExpense) ExpenseRed else IncomeGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (tx.note.isNotBlank()) {
                    Text(
                        text = "${AppStrings.note(language)}: ${tx.note}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "${AppStrings.date(language)}: ${sdf.format(Date(tx.dateMillis))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${AppStrings.paymentMode(language)}: ${tx.paymentMode}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Edit Button
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val itemToEdit = selectedTxForAction
                                selectedTxForAction = null
                                itemToEdit?.let { onEditTransaction(it) }
                            }
                            .testTag("edit_tx_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.edit(language), fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                    }

                    // Delete Button
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showDeleteConfirmDialog = true
                            }
                            .testTag("delete_tx_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.delete(language), fontWeight = FontWeight.Bold, color = ExpenseRed)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showDeleteConfirmDialog && selectedTxForAction != null) {
        val txToDelete = selectedTxForAction!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(AppStrings.delete(language)) },
            text = { Text(AppStrings.confirmDelete(language)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        selectedTxForAction = null
                        onDeleteTransaction(txToDelete)
                    }
                ) {
                    Text(AppStrings.delete(language), color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(AppStrings.cancel(language))
                }
            }
        )
    }
}
