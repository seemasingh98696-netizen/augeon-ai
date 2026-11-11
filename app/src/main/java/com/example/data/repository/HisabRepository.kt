package com.example.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.db.AppDatabase
import com.example.data.model.BudgetEntity
import com.example.data.model.CategoryConstants
import com.example.data.model.SavingsGoalEntity
import com.example.data.model.TransactionEntity
import com.example.util.AppLanguage
import com.example.util.CurrencyFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HisabRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val transactionDao = database.transactionDao()
    private val budgetDao = database.budgetDao()
    private val savingsDao = database.savingsDao()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
    val allGoals: Flow<List<SavingsGoalEntity>> = savingsDao.getAllGoals()

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        withContext(Dispatchers.IO) {
            transactionDao.insertTransaction(transaction)
        }

    suspend fun updateTransaction(transaction: TransactionEntity) =
        withContext(Dispatchers.IO) {
            transactionDao.updateTransaction(transaction)
        }

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        withContext(Dispatchers.IO) {
            transactionDao.deleteTransaction(transaction)
        }

    suspend fun deleteTransactionById(id: Long) =
        withContext(Dispatchers.IO) {
            transactionDao.deleteById(id)
        }

    suspend fun setBudget(category: String, limit: Double) =
        withContext(Dispatchers.IO) {
            budgetDao.setBudget(BudgetEntity(category = category, monthlyLimit = limit))
        }

    suspend fun deleteBudget(category: String) =
        withContext(Dispatchers.IO) {
            budgetDao.deleteBudget(category)
        }

    suspend fun insertGoal(goal: SavingsGoalEntity): Long =
        withContext(Dispatchers.IO) {
            savingsDao.insertGoal(goal)
        }

    suspend fun updateGoal(goal: SavingsGoalEntity) =
        withContext(Dispatchers.IO) {
            savingsDao.updateGoal(goal)
        }

    suspend fun deleteGoal(goal: SavingsGoalEntity) =
        withContext(Dispatchers.IO) {
            savingsDao.deleteGoal(goal)
        }

    suspend fun depositToGoal(goalId: Long, amount: Double) =
        withContext(Dispatchers.IO) {
            val goals = savingsDao.getAllGoals()
            // We can also just update by retrieving or passing goal
        }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        transactionDao.clearAll()
        budgetDao.clearAll()
        savingsDao.clearAll()
    }

    /**
     * Pre-populates the prompt's exact scenario:
     * Starting Balance: ₹2,000
     * Grocery: ₹500
     * Milk: ₹100
     * Electricity: ₹300
     * Remaining Balance: ₹1,100
     */
    suspend fun loadSampleData() = withContext(Dispatchers.IO) {
        clearAllData()
        val now = System.currentTimeMillis()
        val dayMillis = 24L * 60 * 60 * 1000

        // 1. Starting Balance / Initial Income ₹2,000
        transactionDao.insertTransaction(
            TransactionEntity(
                type = "INCOME",
                amount = 2000.0,
                category = "SALARY",
                dateMillis = now - (3 * dayMillis),
                note = "Starting Balance / शुरुआती जमा",
                paymentMode = "CASH"
            )
        )

        // 2. Grocery ₹500
        transactionDao.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 500.0,
                category = "GROCERY",
                dateMillis = now - (2 * dayMillis),
                note = "Atta, Dal, Oil (राशन)",
                paymentMode = "CASH"
            )
        )

        // 3. Milk ₹100
        transactionDao.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 100.0,
                category = "MILK",
                dateMillis = now - (1 * dayMillis),
                note = "Daily Milk (2 Liters)",
                paymentMode = "CASH"
            )
        )

        // 4. Electricity ₹300
        transactionDao.insertTransaction(
            TransactionEntity(
                type = "EXPENSE",
                amount = 300.0,
                category = "ELECTRICITY",
                dateMillis = now,
                note = "Bijli Bill (बिजली का बिल)",
                paymentMode = "UPI"
            )
        )

        // Add helpful default budgets
        budgetDao.setBudget(BudgetEntity(category = "OVERALL", monthlyLimit = 15000.0))
        budgetDao.setBudget(BudgetEntity(category = "GROCERY", monthlyLimit = 4000.0))
        budgetDao.setBudget(BudgetEntity(category = "MILK", monthlyLimit = 2000.0))
        budgetDao.setBudget(BudgetEntity(category = "ELECTRICITY", monthlyLimit = 1500.0))

        // Add a sample savings goal (Gullak)
        savingsDao.insertGoal(
            SavingsGoalEntity(
                title = "Diwali Gold Coin (दीवाली सोने का सिक्का)",
                targetAmount = 10000.0,
                savedAmount = 2500.0,
                targetDateMillis = now + (90 * dayMillis),
                note = "Family Savings"
            )
        )
    }

    /**
     * Exports transactions into CSV format and returns an Intent to share via WhatsApp, Mail, etc.
     */
    suspend fun exportTransactionsToCsv(
        transactions: List<TransactionEntity>,
        language: AppLanguage
    ): Intent? = withContext(Dispatchers.IO) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val sb = StringBuilder()
            // Header
            sb.append("Tariqh (Date),Prakar (Type),Shreni (Category),Rakam (Amount INR),Madhyam (Mode),Vivran (Note)\n")

            for (t in transactions) {
                val cat = CategoryConstants.getCategory(t.category, t.type == "EXPENSE")
                val catName = if (language == AppLanguage.HINDI) cat.nameHi else cat.nameEn
                val typeName = if (t.type == "INCOME") "Aay (Income)" else "Kharch (Expense)"
                val cleanNote = t.note.replace(",", " ").replace("\n", " ")
                val dateStr = sdf.format(Date(t.dateMillis))

                sb.append("\"$dateStr\",\"$typeName\",\"$catName\",${t.amount},\"${t.paymentMode}\",\"$cleanNote\"\n")
            }

            val fileName = "GharKaHisab_Report_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())}.csv"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { fos ->
                fos.write(sb.toString().toByteArray())
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Ghar Ka Hisab Report")
                putExtra(Intent.EXTRA_TEXT, "Ghar Ka Hisab - Monthly Financial Statement")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            Intent.createChooser(shareIntent, "Share Ghar Ka Hisab Report").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Exports all database data to a JSON backup string
     */
    suspend fun exportBackupJson(
        transactions: List<TransactionEntity>,
        budgets: List<BudgetEntity>,
        goals: List<SavingsGoalEntity>
    ): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "GharKaHisab")
        root.put("version", 1)
        root.put("backupDate", System.currentTimeMillis())

        val txArray = JSONArray()
        for (t in transactions) {
            val item = JSONObject()
            item.put("type", t.type)
            item.put("amount", t.amount)
            item.put("category", t.category)
            item.put("dateMillis", t.dateMillis)
            item.put("note", t.note)
            item.put("paymentMode", t.paymentMode)
            txArray.put(item)
        }
        root.put("transactions", txArray)

        val bArray = JSONArray()
        for (b in budgets) {
            val item = JSONObject()
            item.put("category", b.category)
            item.put("monthlyLimit", b.monthlyLimit)
            bArray.put(item)
        }
        root.put("budgets", bArray)

        val gArray = JSONArray()
        for (g in goals) {
            val item = JSONObject()
            item.put("title", g.title)
            item.put("targetAmount", g.targetAmount)
            item.put("savedAmount", g.savedAmount)
            item.put("targetDateMillis", g.targetDateMillis)
            item.put("note", g.note)
            gArray.put(item)
        }
        root.put("goals", gArray)

        root.toString(2)
    }

    /**
     * Restores database data from JSON backup string
     */
    suspend fun restoreFromJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("transactions")) return@withContext false

            clearAllData()

            val txArray = root.getJSONArray("transactions")
            val txList = mutableListOf<TransactionEntity>()
            for (i in 0 until txArray.length()) {
                val obj = txArray.getJSONObject(i)
                txList.add(
                    TransactionEntity(
                        type = obj.optString("type", "EXPENSE"),
                        amount = obj.optDouble("amount", 0.0),
                        category = obj.optString("category", "OTHER"),
                        dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                        note = obj.optString("note", ""),
                        paymentMode = obj.optString("paymentMode", "CASH")
                    )
                )
            }
            transactionDao.insertTransactions(txList)

            if (root.has("budgets")) {
                val bArray = root.getJSONArray("budgets")
                val bList = mutableListOf<BudgetEntity>()
                for (i in 0 until bArray.length()) {
                    val obj = bArray.getJSONObject(i)
                    bList.add(
                        BudgetEntity(
                            category = obj.getString("category"),
                            monthlyLimit = obj.getDouble("monthlyLimit")
                        )
                    )
                }
                budgetDao.setBudgets(bList)
            }

            if (root.has("goals")) {
                val gArray = root.getJSONArray("goals")
                val gList = mutableListOf<SavingsGoalEntity>()
                for (i in 0 until gArray.length()) {
                    val obj = gArray.getJSONObject(i)
                    gList.add(
                        SavingsGoalEntity(
                            title = obj.getString("title"),
                            targetAmount = obj.getDouble("targetAmount"),
                            savedAmount = obj.optDouble("savedAmount", 0.0),
                            targetDateMillis = obj.optLong("targetDateMillis", 0L),
                            note = obj.optString("note", "")
                        )
                    )
                }
                savingsDao.insertGoals(gList)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
