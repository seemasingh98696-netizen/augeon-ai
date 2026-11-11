package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.HisabRepository
import com.example.util.AppLanguage
import com.example.util.PreferencesManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class MonthlySummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyRemaining: Double = 0.0,
    val totalSavings: Double = 0.0
)

class HisabViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = HisabRepository(database, application)
    val prefs = PreferencesManager(application)

    // Language & Security
    private val _language = MutableStateFlow(prefs.language)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isPinEnabled = MutableStateFlow(prefs.isPinEnabled)
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private val _isUnlocked = MutableStateFlow(!prefs.isPinEnabled)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    // Navigation
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Base Database Flows
    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savingsGoals: StateFlow<List<SavingsGoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Summary calculations
    val summary: StateFlow<MonthlySummary> = combine(transactions, savingsGoals) { txList, goals ->
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        var allTimeIncome = 0.0
        var allTimeExpense = 0.0
        var monthIncome = 0.0
        var monthExpense = 0.0

        for (tx in txList) {
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
            val isCurrentMonth = txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.MONTH) == currentMonth

            if (tx.type == "INCOME") {
                allTimeIncome += tx.amount
                if (isCurrentMonth) monthIncome += tx.amount
            } else {
                allTimeExpense += tx.amount
                if (isCurrentMonth) monthExpense += tx.amount
            }
        }

        val totalSaved = goals.sumOf { it.savedAmount }

        MonthlySummary(
            totalIncome = allTimeIncome,
            totalExpense = allTimeExpense,
            netBalance = allTimeIncome - allTimeExpense,
            monthlyIncome = monthIncome,
            monthlyExpense = monthExpense,
            monthlyRemaining = monthIncome - monthExpense,
            totalSavings = totalSaved
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthlySummary())

    // Transaction Filters
    val searchQuery = MutableStateFlow("")
    val filterType = MutableStateFlow("ALL") // "ALL", "INCOME", "EXPENSE"
    val filterTimeRange = MutableStateFlow("ALL") // "ALL", "TODAY", "WEEK", "MONTH"
    val filterCategory = MutableStateFlow("ALL")

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        transactions,
        searchQuery,
        filterType,
        filterTimeRange,
        filterCategory
    ) { txList, query, type, timeRange, cat ->
        val nowCal = Calendar.getInstance()
        val currentYear = nowCal.get(Calendar.YEAR)
        val currentDayOfYear = nowCal.get(Calendar.DAY_OF_YEAR)
        val currentWeekOfYear = nowCal.get(Calendar.WEEK_OF_YEAR)
        val currentMonth = nowCal.get(Calendar.MONTH)

        txList.filter { tx ->
            // Type filter
            val matchesType = when (type) {
                "INCOME" -> tx.type == "INCOME"
                "EXPENSE" -> tx.type == "EXPENSE"
                else -> true
            }

            // Category filter
            val matchesCat = cat == "ALL" || tx.category.equals(cat, ignoreCase = true)

            // Search query filter
            val matchesQuery = query.isBlank() ||
                    tx.note.contains(query, ignoreCase = true) ||
                    tx.category.contains(query, ignoreCase = true) ||
                    tx.amount.toString().contains(query)

            // Time range filter
            val matchesTime = if (timeRange == "ALL") {
                true
            } else {
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
                when (timeRange) {
                    "TODAY" -> txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.DAY_OF_YEAR) == currentDayOfYear
                    "WEEK" -> txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.WEEK_OF_YEAR) == currentWeekOfYear
                    "MONTH" -> txCal.get(Calendar.YEAR) == currentYear && txCal.get(Calendar.MONTH) == currentMonth
                    else -> true
                }
            }

            matchesType && matchesCat && matchesQuery && matchesTime
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Messages / Snackbar
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Dialog state holders
    val showAddDialog = MutableStateFlow(false)
    val addDialogDefaultType = MutableStateFlow("EXPENSE")
    val editingTransaction = MutableStateFlow<TransactionEntity?>(null)

    val showBudgetDialog = MutableStateFlow(false)
    val editingBudgetCategory = MutableStateFlow<String?>(null)

    val showGoalDialog = MutableStateFlow(false)
    val editingGoal = MutableStateFlow<SavingsGoalEntity?>(null)

    val showDepositDialog = MutableStateFlow(false)
    val activeDepositGoal = MutableStateFlow<SavingsGoalEntity?>(null)

    val showSettingsDialog = MutableStateFlow(false)
    val showPinSetupDialog = MutableStateFlow(false)

    init {
        // First turn initialization: if app has no data and hasn't loaded sample yet,
        // automatically populate the initial demo data as requested in prompt:
        // Starting Balance: ₹2,000, Grocery: ₹500, Milk: ₹100, Electricity: ₹300, Remaining Balance: ₹1,100!
        viewModelScope.launch {
            if (!prefs.isSampleLoaded) {
                repository.loadSampleData()
                prefs.isSampleLoaded = true
            }
        }
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun toggleLanguage() {
        val next = if (_language.value == AppLanguage.HINDI) AppLanguage.ENGLISH else AppLanguage.HINDI
        _language.value = next
        prefs.language = next
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        prefs.language = lang
    }

    fun unlockApp(pin: String): Boolean {
        if (!prefs.isPinEnabled || prefs.verifyPin(pin)) {
            _isUnlocked.value = true
            return true
        }
        return false
    }

    fun setupPin(newPin: String) {
        prefs.setPin(newPin)
        _isPinEnabled.value = true
        _isUnlocked.value = true
        emitMessage(if (_language.value == AppLanguage.HINDI) "पिन सफलतापूर्वक सुरक्षित हो गया" else "PIN enabled successfully")
    }

    fun removePin() {
        prefs.disablePin()
        _isPinEnabled.value = false
        _isUnlocked.value = true
        emitMessage(if (_language.value == AppLanguage.HINDI) "पिन सुरक्षा हटा दी गई" else "PIN lock disabled")
    }

    fun saveTransaction(
        id: Long = 0,
        type: String,
        amount: Double,
        category: String,
        dateMillis: Long,
        note: String,
        paymentMode: String
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                id = id,
                type = type,
                amount = amount,
                category = category,
                dateMillis = dateMillis,
                note = note,
                paymentMode = paymentMode
            )
            if (id == 0L) {
                repository.insertTransaction(entity)
                emitMessage(if (_language.value == AppLanguage.HINDI) "लेन-देन सुरक्षित हुआ" else "Transaction saved")
            } else {
                repository.updateTransaction(entity)
                emitMessage(if (_language.value == AppLanguage.HINDI) "लेन-देन अपडेट हुआ" else "Transaction updated")
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            emitMessage(if (_language.value == AppLanguage.HINDI) "लेन-देन हटा दिया गया" else "Transaction deleted")
        }
    }

    fun saveBudget(category: String, limit: Double) {
        viewModelScope.launch {
            repository.setBudget(category, limit)
            emitMessage(if (_language.value == AppLanguage.HINDI) "बजट तय हो गया" else "Budget set successfully")
        }
    }

    fun deleteBudget(category: String) {
        viewModelScope.launch {
            repository.deleteBudget(category)
            emitMessage(if (_language.value == AppLanguage.HINDI) "बजट हटा दिया गया" else "Budget removed")
        }
    }

    fun saveSavingsGoal(
        id: Long = 0,
        title: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDateMillis: Long,
        note: String
    ) {
        viewModelScope.launch {
            val entity = SavingsGoalEntity(
                id = id,
                title = title,
                targetAmount = targetAmount,
                savedAmount = savedAmount,
                targetDateMillis = targetDateMillis,
                note = note
            )
            if (id == 0L) {
                repository.insertGoal(entity)
                emitMessage(if (_language.value == AppLanguage.HINDI) "नया बचत लक्ष्य बन गया" else "Savings goal created")
            } else {
                repository.updateGoal(entity)
                emitMessage(if (_language.value == AppLanguage.HINDI) "बचत लक्ष्य अपडेट हुआ" else "Goal updated")
            }
        }
    }

    fun updateGoalSavings(goal: SavingsGoalEntity, deltaAmount: Double) {
        viewModelScope.launch {
            val newAmount = kotlin.math.max(0.0, goal.savedAmount + deltaAmount)
            repository.updateGoal(goal.copy(savedAmount = newAmount))
            val msg = if (deltaAmount >= 0) {
                if (_language.value == AppLanguage.HINDI) "गुल्लक में ₹${deltaAmount.toInt()} जमा किए गए" else "Deposited ₹${deltaAmount.toInt()}"
            } else {
                if (_language.value == AppLanguage.HINDI) "गुल्लक से ₹${kotlin.math.abs(deltaAmount).toInt()} निकाले गए" else "Withdrew ₹${kotlin.math.abs(deltaAmount).toInt()}"
            }
            emitMessage(msg)
        }
    }

    fun deleteGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            emitMessage(if (_language.value == AppLanguage.HINDI) "बचत लक्ष्य हटा दिया गया" else "Savings goal removed")
        }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.loadSampleData()
            emitMessage(if (_language.value == AppLanguage.HINDI) "उदाहरण डेटा सफलतापूर्वक लोड हुआ" else "Sample demo data loaded")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            emitMessage(if (_language.value == AppLanguage.HINDI) "सभी डेटा साफ़ कर दिया गया" else "All data reset")
        }
    }

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            val intent = repository.exportTransactionsToCsv(transactions.value, _language.value)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                emitMessage(if (_language.value == AppLanguage.HINDI) "रिपोर्ट बनाने में समस्या आई" else "Error generating CSV report")
            }
        }
    }

    fun shareBackup(context: Context) {
        viewModelScope.launch {
            val json = repository.exportBackupJson(transactions.value, budgets.value, savingsGoals.value)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Ghar Ka Hisab Backup")
                putExtra(Intent.EXTRA_TEXT, json)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Ghar Ka Hisab Backup"))
        }
    }

    fun restoreBackup(jsonString: String) {
        viewModelScope.launch {
            val success = repository.restoreFromJson(jsonString)
            if (success) {
                emitMessage(if (_language.value == AppLanguage.HINDI) "डेटा सफलतापूर्वक रीस्टोर हो गया" else "Backup restored successfully")
            } else {
                emitMessage(if (_language.value == AppLanguage.HINDI) "अमान्य बैकअप डेटा" else "Invalid backup data")
            }
        }
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _userMessage.emit(msg)
        }
    }
}
