package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.dialogs.AddSavingsGoalDialog
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.dialogs.BudgetDialog
import com.example.ui.dialogs.GoalDepositDialog
import com.example.ui.dialogs.PinSetupDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.screens.BudgetScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PinLockScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SavingsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SaffronSecondary
import com.example.ui.viewmodel.HisabViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: HisabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: HisabViewModel) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsStateWithLifecycle()
    val isUnlocked by viewModel.isUnlocked.collectAsStateWithLifecycle()
    val isPinEnabled by viewModel.isPinEnabled.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val allTx by viewModel.transactions.collectAsStateWithLifecycle()
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val filterTimeRange by viewModel.filterTimeRange.collectAsStateWithLifecycle()

    val showAddDialog by viewModel.showAddDialog.collectAsStateWithLifecycle()
    val addDialogDefaultType by viewModel.addDialogDefaultType.collectAsStateWithLifecycle()
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()

    val showBudgetDialog by viewModel.showBudgetDialog.collectAsStateWithLifecycle()
    val editingBudgetCategory by viewModel.editingBudgetCategory.collectAsStateWithLifecycle()

    val showGoalDialog by viewModel.showGoalDialog.collectAsStateWithLifecycle()
    val editingGoal by viewModel.editingGoal.collectAsStateWithLifecycle()

    val showDepositDialog by viewModel.showDepositDialog.collectAsStateWithLifecycle()
    val activeDepositGoal by viewModel.activeDepositGoal.collectAsStateWithLifecycle()

    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showPinSetupDialog by viewModel.showPinSetupDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Security Gate: If PIN lock is active and not unlocked yet
    if (!isUnlocked) {
        PinLockScreen(
            language = language,
            onUnlock = { pin -> viewModel.unlockApp(pin) },
            onForgotPin = { viewModel.removePin() }
        )
        return
    }

    // Back handling: if in non-dashboard tab, back moves to Dashboard (tab 0)
    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "Ghar Ka Hisab Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppStrings.appTitle(language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (language == AppLanguage.HINDI) "आसान घरेलू हिसाब" else "Simple Home Money Tracker",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // One-Tap Quick Language Switcher Chip [हिन्दी | English]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                            .clickable { viewModel.toggleLanguage() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("quick_language_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (language == AppLanguage.HINDI) "English" else "हिन्दी",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { viewModel.showSettingsDialog.value = true },
                        modifier = Modifier.testTag("top_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                val navItems = listOf(
                    Triple(0, AppStrings.navDashboard(language), Pair(Icons.Filled.Home, Icons.Outlined.Home)),
                    Triple(1, AppStrings.navTransactions(language), Pair(Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong)),
                    Triple(2, AppStrings.navBudget(language), Pair(Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)),
                    Triple(3, AppStrings.navSavings(language), Pair(Icons.Filled.Savings, Icons.Outlined.Savings)),
                    Triple(4, AppStrings.navReports(language), Pair(Icons.Filled.Assessment, Icons.Outlined.Assessment))
                )

                for ((idx, label, icons) in navItems) {
                    val isSelected = currentTab == idx
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(idx) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) icons.first else icons.second,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            selectedTextColor = GreenPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_$idx")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(
                    language = language,
                    summary = summary,
                    transactions = allTx,
                    budgets = budgets,
                    onAddExpenseClick = {
                        viewModel.addDialogDefaultType.value = "EXPENSE"
                        viewModel.editingTransaction.value = null
                        viewModel.showAddDialog.value = true
                    },
                    onAddIncomeClick = {
                        viewModel.addDialogDefaultType.value = "INCOME"
                        viewModel.editingTransaction.value = null
                        viewModel.showAddDialog.value = true
                    },
                    onViewAllTransactions = { viewModel.setTab(1) },
                    onTransactionClick = { tx ->
                        viewModel.editingTransaction.value = tx
                        viewModel.showAddDialog.value = true
                    },
                    onOpenBudgetTab = { viewModel.setTab(2) }
                )
                1 -> TransactionsScreen(
                    language = language,
                    transactions = transactions,
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterTimeRange = filterTimeRange,
                    onSearchChange = { viewModel.searchQuery.value = it },
                    onFilterTypeChange = { viewModel.filterType.value = it },
                    onFilterTimeRangeChange = { viewModel.filterTimeRange.value = it },
                    onAddClick = {
                        viewModel.addDialogDefaultType.value = "EXPENSE"
                        viewModel.editingTransaction.value = null
                        viewModel.showAddDialog.value = true
                    },
                    onEditTransaction = { tx ->
                        viewModel.editingTransaction.value = tx
                        viewModel.showAddDialog.value = true
                    },
                    onDeleteTransaction = { tx ->
                        viewModel.deleteTransaction(tx)
                    }
                )
                2 -> BudgetScreen(
                    language = language,
                    budgets = budgets,
                    transactions = allTx,
                    onSetOverallBudget = {
                        viewModel.editingBudgetCategory.value = "OVERALL"
                        viewModel.showBudgetDialog.value = true
                    },
                    onSetCategoryBudget = { catId, _ ->
                        viewModel.editingBudgetCategory.value = catId
                        viewModel.showBudgetDialog.value = true
                    }
                )
                3 -> SavingsScreen(
                    language = language,
                    goals = savingsGoals,
                    onAddGoalClick = {
                        viewModel.editingGoal.value = null
                        viewModel.showGoalDialog.value = true
                    },
                    onEditGoal = { goal ->
                        viewModel.editingGoal.value = goal
                        viewModel.showGoalDialog.value = true
                    },
                    onDeleteGoal = { goal ->
                        viewModel.deleteGoal(goal)
                    },
                    onDepositOrWithdrawClick = { goal ->
                        viewModel.activeDepositGoal.value = goal
                        viewModel.showDepositDialog.value = true
                    }
                )
                4 -> ReportsScreen(
                    language = language,
                    transactions = allTx,
                    onExportCsv = { viewModel.exportCsv(context) }
                )
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        AddTransactionDialog(
            language = language,
            initialType = addDialogDefaultType,
            editingTransaction = editingTransaction,
            onDismiss = {
                viewModel.showAddDialog.value = false
                viewModel.editingTransaction.value = null
            },
            onSave = { id, type, amount, category, dateMillis, note, paymentMode ->
                viewModel.saveTransaction(id, type, amount, category, dateMillis, note, paymentMode)
            }
        )
    }

    if (showBudgetDialog) {
        val catKey = editingBudgetCategory ?: "OVERALL"
        val currentLimit = budgets.find { it.category.equals(catKey, ignoreCase = true) }?.monthlyLimit ?: 0.0
        BudgetDialog(
            language = language,
            categoryKey = catKey,
            currentLimit = currentLimit,
            onDismiss = {
                viewModel.showBudgetDialog.value = false
                viewModel.editingBudgetCategory.value = null
            },
            onSave = { category, limit ->
                viewModel.saveBudget(category, limit)
            }
        )
    }

    if (showGoalDialog) {
        AddSavingsGoalDialog(
            language = language,
            editingGoal = editingGoal,
            onDismiss = {
                viewModel.showGoalDialog.value = false
                viewModel.editingGoal.value = null
            },
            onSave = { id, title, targetAmount, savedAmount, targetDateMillis, note ->
                viewModel.saveSavingsGoal(id, title, targetAmount, savedAmount, targetDateMillis, note)
            }
        )
    }

    if (showDepositDialog && activeDepositGoal != null) {
        GoalDepositDialog(
            language = language,
            goal = activeDepositGoal!!,
            onDismiss = {
                viewModel.showDepositDialog.value = false
                viewModel.activeDepositGoal.value = null
            },
            onUpdateSavings = { goal, delta ->
                viewModel.updateGoalSavings(goal, delta)
            }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            language = language,
            isPinEnabled = isPinEnabled,
            onLanguageChange = { newLang -> viewModel.setLanguage(newLang) },
            onDismiss = { viewModel.showSettingsDialog.value = false },
            onOpenPinSetup = { viewModel.showPinSetupDialog.value = true },
            onDisablePin = { viewModel.removePin() },
            onLoadSampleData = { viewModel.loadSampleData() },
            onClearAllData = { viewModel.clearAllData() },
            onExportBackup = { viewModel.shareBackup(context) },
            onRestoreBackup = { json -> viewModel.restoreBackup(json) }
        )
    }

    if (showPinSetupDialog) {
        PinSetupDialog(
            language = language,
            onDismiss = { viewModel.showPinSetupDialog.value = false },
            onPinSet = { newPin -> viewModel.setupPin(newPin) }
        )
    }
}
