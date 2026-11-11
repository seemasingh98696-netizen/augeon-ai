package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryConstants
import com.example.data.model.TransactionEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedBg
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenBg
import com.example.ui.theme.SaffronSecondary
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.CurrencyFormatter
import java.util.Calendar

@Composable
fun ReportsScreen(
    language: AppLanguage,
    transactions: List<TransactionEntity>,
    onExportCsv: () -> Unit
) {
    var selectedTimePeriod by remember { mutableStateOf("MONTH") } // "TODAY", "WEEK", "MONTH", "ALL"

    // Filter transactions based on selected period
    val now = Calendar.getInstance()
    val curYear = now.get(Calendar.YEAR)
    val curDayOfYear = now.get(Calendar.DAY_OF_YEAR)
    val curWeek = now.get(Calendar.WEEK_OF_YEAR)
    val curMonth = now.get(Calendar.MONTH)

    val filteredList = transactions.filter { tx ->
        if (selectedTimePeriod == "ALL") return@filter true
        val cal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
        when (selectedTimePeriod) {
            "TODAY" -> cal.get(Calendar.YEAR) == curYear && cal.get(Calendar.DAY_OF_YEAR) == curDayOfYear
            "WEEK" -> cal.get(Calendar.YEAR) == curYear && cal.get(Calendar.WEEK_OF_YEAR) == curWeek
            "MONTH" -> cal.get(Calendar.YEAR) == curYear && cal.get(Calendar.MONTH) == curMonth
            else -> true
        }
    }

    val totalIncome = filteredList.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalExpense = filteredList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val netSavings = totalIncome - totalExpense

    // Expenses by Category
    val expensesByCategory = filteredList.filter { it.type == "EXPENSE" }
        .groupBy { it.category.uppercase() }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.reportsTitle(language),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Export Button
                Button(
                    onClick = onExportCsv,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("export_csv_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CSV",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Time Period Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val periods = listOf(
                    "TODAY" to AppStrings.today(language),
                    "WEEK" to AppStrings.thisWeek(language),
                    "MONTH" to AppStrings.thisMonth(language),
                    "ALL" to AppStrings.all(language)
                )
                items(periods) { (code, label) ->
                    val isSelected = selectedTimePeriod == code
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTimePeriod = code },
                        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = GreenPrimary
                        )
                    )
                }
            }
        }

        // Summary 3-Card Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Income
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = IncomeGreenBg),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (language == AppLanguage.HINDI) "आय" else "Income", fontSize = 12.sp, color = IncomeGreen, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatRupee(totalIncome),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = IncomeGreen
                        )
                    }
                }

                // Expense
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ExpenseRedBg),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (language == AppLanguage.HINDI) "खर्च" else "Expense", fontSize = 12.sp, color = ExpenseRed, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatRupee(totalExpense),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = ExpenseRed
                        )
                    }
                }

                // Net Savings
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (netSavings >= 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = if (language == AppLanguage.HINDI) "बचत" else "Savings", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatRupee(netSavings),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (netSavings >= 0) GreenPrimary else ExpenseRed
                        )
                    }
                }
            }
        }

        // 1. Income vs Expense Comparison Chart Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("income_vs_expense_chart_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppStrings.incomeVsExpense(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val maxAmount = kotlin.math.max(1.0, kotlin.math.max(totalIncome, totalExpense))
                    val incomeRatio = (totalIncome / maxAmount).toFloat()
                    val expenseRatio = (totalExpense / maxAmount).toFloat()

                    // Income Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (language == AppLanguage.HINDI) "कुल आय (Income)" else "Total Income", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = CurrencyFormatter.formatRupee(totalIncome), fontWeight = FontWeight.Bold, color = IncomeGreen, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { incomeRatio },
                            color = IncomeGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expense Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (language == AppLanguage.HINDI) "कुल खर्च (Expense)" else "Total Expense", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = CurrencyFormatter.formatRupee(totalExpense), fontWeight = FontWeight.Bold, color = ExpenseRed, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { expenseRatio },
                            color = ExpenseRed,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }
                }
            }
        }

        // 2. Expense Category Breakdown Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("expense_breakdown_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.expenseBreakdown(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Icon(Icons.Default.PieChart, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (expensesByCategory.isEmpty()) {
                        Text(
                            text = if (language == AppLanguage.HINDI) "इस अवधि में कोई खर्च नहीं हुआ" else "No expenses recorded in this period",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Multi-segment Proportional Distribution Bar (Mother-friendly stacked bar)
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp)
                                .clip(RoundedCornerShape(9.dp))
                        ) {
                            var currentX = 0f
                            val canvasWidth = size.width
                            val total = totalExpense.toFloat().coerceAtLeast(1f)

                            for ((catId, amount) in expensesByCategory) {
                                val cat = CategoryConstants.getCategory(catId, true)
                                val segmentWidth = (amount.toFloat() / total) * canvasWidth
                                drawRect(
                                    color = cat.color,
                                    topLeft = Offset(currentX, 0f),
                                    size = Size(segmentWidth, size.height)
                                )
                                currentX += segmentWidth
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Details List with Percentages
                        for ((catId, amount) in expensesByCategory) {
                            val cat = CategoryConstants.getCategory(catId, true)
                            val catName = if (language == AppLanguage.HINDI) cat.nameHi else cat.nameEn
                            val percentage = if (totalExpense > 0) ((amount / totalExpense) * 100).toInt() else 0

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(cat.color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = catName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$percentage%",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatRupee(amount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Export & Share Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppStrings.exportCsv(language),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (language == AppLanguage.HINDI) "व्हाट्सएप या ईमेल पर हिसाब शेयर करें" else "Share via WhatsApp or Email",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onExportCsv,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (language == AppLanguage.HINDI) "शेयर करें" else "Share")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
