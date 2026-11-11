package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SavingsGoalEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.SavingsGold
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun AddSavingsGoalDialog(
    language: AppLanguage,
    editingGoal: SavingsGoalEntity? = null,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDateMillis: Long,
        note: String
    ) -> Unit
) {
    var title by remember { mutableStateOf(editingGoal?.title ?: "") }
    var targetText by remember {
        mutableStateOf(if (editingGoal != null) editingGoal.targetAmount.toInt().toString() else "")
    }
    var savedText by remember {
        mutableStateOf(if (editingGoal != null) editingGoal.savedAmount.toInt().toString() else "0")
    }
    var note by remember { mutableStateOf(editingGoal?.note ?: "") }
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = SavingsGold)
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text(
                            text = if (editingGoal != null) AppStrings.edit(language) else AppStrings.addSavingsGoal(language),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(AppStrings.goalName(language)) },
                    placeholder = {
                        Text(
                            if (language == AppLanguage.HINDI) "जैसे: दीवाली खर्च, नई अलमारी, सोना"
                            else "e.g., Diwali shopping, Gold, Fridge"
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("goal_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = targetText,
                    onValueChange = {
                        targetText = it.filter { ch -> ch.isDigit() || ch == '.' }
                        showError = false
                    },
                    label = { Text(AppStrings.targetAmount(language)) },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = SavingsGold, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = showError,
                    modifier = Modifier.fillMaxWidth().testTag("goal_target_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = savedText,
                    onValueChange = {
                        savedText = it.filter { ch -> ch.isDigit() || ch == '.' }
                    },
                    label = { Text(AppStrings.savedAmount(language)) },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = GreenPrimary, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("goal_saved_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(AppStrings.note(language)) },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val target = targetText.toDoubleOrNull()
                        val saved = savedText.toDoubleOrNull() ?: 0.0
                        if (title.isBlank() || target == null || target <= 0) {
                            showError = true
                            return@Button
                        }
                        onSave(
                            editingGoal?.id ?: 0L,
                            title.trim(),
                            target,
                            saved,
                            System.currentTimeMillis() + (90L * 24 * 3600 * 1000),
                            note.trim()
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SavingsGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_goal_button")
                ) {
                    Text(
                        text = AppStrings.save(language),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun GoalDepositDialog(
    language: AppLanguage,
    goal: SavingsGoalEntity,
    onDismiss: () -> Unit,
    onUpdateSavings: (goal: SavingsGoalEntity, deltaAmount: Double) -> Unit
) {
    var isDeposit by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle Deposit (+) vs Withdraw (-)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDeposit) IncomeGreen else Color.Transparent)
                            .clickable { isDeposit = true }
                            .testTag("toggle_deposit"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = if (isDeposit) Color.White else MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text(AppStrings.depositMoney(language), fontWeight = FontWeight.Bold, color = if (isDeposit) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isDeposit) ExpenseRed else Color.Transparent)
                            .clickable { isDeposit = false }
                            .testTag("toggle_withdraw"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = if (!isDeposit) Color.White else MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text(AppStrings.withdrawMoney(language), fontWeight = FontWeight.Bold, color = if (!isDeposit) Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { ch -> ch.isDigit() || ch == '.' }
                        showError = false
                    },
                    label = { Text(AppStrings.amount(language)) },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = if (isDeposit) IncomeGreen else ExpenseRed, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = showError,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("deposit_amount_input")
                )

                // Quick buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickAdd = listOf(100, 500, 1000, 2000)
                    for (qa in quickAdd) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable {
                                    val cur = amountText.toDoubleOrNull() ?: 0.0
                                    amountText = (cur + qa).toInt().toString()
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+₹$qa", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val parsed = amountText.toDoubleOrNull()
                        if (parsed == null || parsed <= 0) {
                            showError = true
                            return@Button
                        }
                        val delta = if (isDeposit) parsed else -parsed
                        onUpdateSavings(goal, delta)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDeposit) IncomeGreen else ExpenseRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_deposit_button")
                ) {
                    Text(
                        text = if (isDeposit) AppStrings.depositMoney(language) else AppStrings.withdrawMoney(language),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
