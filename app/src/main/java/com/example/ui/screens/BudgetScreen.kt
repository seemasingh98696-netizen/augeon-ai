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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryConstants
import com.example.data.model.TransactionEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.SaffronSecondary
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.CurrencyFormatter
import java.util.Calendar

@Composable
fun BudgetScreen(
    language: AppLanguage,
    budgets: List<BudgetEntity>,
    transactions: List<TransactionEntity>,
    onSetOverallBudget: () -> Unit,
    onSetCategoryBudget: (String, Double) -> Unit
) {
    // Calculate current month expenses
    val now = Calendar.getInstance()
    val curYear = now.get(Calendar.YEAR)
    val curMonth = now.get(Calendar.MONTH)

    val currentMonthExpenses = transactions.filter {
        if (it.type != "EXPENSE") return@filter false
        val cal = Calendar.getInstance().apply { timeInMillis = it.dateMillis }
        cal.get(Calendar.YEAR) == curYear && cal.get(Calendar.MONTH) == curMonth
    }

    val totalSpent = currentMonthExpenses.sumOf { it.amount }
    val overallBudget = budgets.find { it.category == "OVERALL" }?.monthlyLimit ?: 0.0

    // Expenses by category
    val spentByCategory = currentMonthExpenses.groupBy { it.category.uppercase() }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = AppStrings.budgetTitle(language),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Overall Monthly Budget Hero Card
        item {
            val progress = if (overallBudget > 0) (totalSpent / overallBudget).coerceIn(0.0, 1.0).toFloat() else 0f
            val isExceeded = overallBudget > 0 && totalSpent > overallBudget
            val remaining = if (overallBudget > 0) kotlin.math.max(0.0, overallBudget - totalSpent) else 0.0

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExceeded) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("overall_budget_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.totalMonthlyBudget(language),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (overallBudget > 0) CurrencyFormatter.formatRupee(overallBudget) else (if (language == AppLanguage.HINDI) "तय नहीं किया" else "Not set"),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onSetOverallBudget,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("set_overall_budget_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (overallBudget > 0) AppStrings.edit(language) else (if (language == AppLanguage.HINDI) "तय करें" else "Set"))
                        }
                    }

                    if (overallBudget > 0) {
                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            color = if (isExceeded) ExpenseRed else if (progress > 0.8f) SaffronSecondary else GreenPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${AppStrings.spent(language)}: ${CurrencyFormatter.formatRupee(totalSpent)} (${(progress * 100).toInt()}%)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isExceeded) "${AppStrings.overBudget(language)}: ${CurrencyFormatter.formatRupee(totalSpent - overallBudget)}"
                                else "${AppStrings.remainingBudget(language)}: ${CurrencyFormatter.formatRupee(remaining)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExceeded) ExpenseRed else GreenPrimary
                            )
                        }
                    }
                }
            }
        }

        // Category-wise Budgets Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.categoryWiseBudget(language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // List of Category Budgets (Grocery, Milk, Electricity, etc.)
        items(CategoryConstants.EXPENSE_CATEGORIES) { cat ->
            val limit = budgets.find { it.category.equals(cat.id, ignoreCase = true) }?.monthlyLimit ?: 0.0
            val spent = spentByCategory[cat.id.uppercase()] ?: 0.0
            val catName = if (language == AppLanguage.HINDI) cat.nameHi else cat.nameEn
            val progress = if (limit > 0) (spent / limit).coerceIn(0.0, 1.0).toFloat() else 0f
            val isExceeded = limit > 0 && spent > limit

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSetCategoryBudget(cat.id, limit) }
                    .testTag("budget_item_${cat.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cat.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(cat.icon, contentDescription = null, tint = cat.color, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = catName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    text = if (limit > 0) "${if (language == AppLanguage.HINDI) "बजट" else "Budget"}: ${CurrencyFormatter.formatRupee(limit)}"
                                    else (if (language == AppLanguage.HINDI) "बजट तय नहीं है" else "No limit set"),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${AppStrings.spent(language)}: ${CurrencyFormatter.formatRupee(spent)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isExceeded) ExpenseRed else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (limit > 0) (if (isExceeded) AppStrings.overBudget(language) else "${AppStrings.remainingBudget(language)}: ${CurrencyFormatter.formatRupee(limit - spent)}")
                                else (if (language == AppLanguage.HINDI) "+ बजट जोड़ें" else "+ Set Limit"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isExceeded) ExpenseRed else GreenPrimary
                            )
                        }
                    }

                    if (limit > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            color = if (isExceeded) ExpenseRed else if (progress > 0.85f) SaffronSecondary else cat.color,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
