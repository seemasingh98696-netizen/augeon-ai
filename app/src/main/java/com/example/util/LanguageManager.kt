package com.example.util

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    HINDI("hi", "Hindi", "हिन्दी"),
    ENGLISH("en", "English", "English")
}

object AppStrings {
    // Nav Items
    fun navDashboard(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "मुख्य" else "Dashboard"
    fun navTransactions(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लेन-देन" else "History"
    fun navBudget(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बजट" else "Budget"
    fun navSavings(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "गुल्लक" else "Savings"
    fun navReports(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रिपोर्ट" else "Reports"

    // Dashboard Strings
    fun appTitle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "घर का हिसाब" else "Ghar Ka Hisab"
    fun motherWelcome(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "नमस्ते माँ! आज का हिसाब" else "Welcome! Home Money Tracker"
    fun totalBalance(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कुल बचा हुआ पैसा" else "Remaining Balance"
    fun monthlyIncome(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "इस महीने की आय" else "Monthly Income"
    fun monthlyExpense(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "इस महीने का खर्च" else "Monthly Expense"
    fun totalSavings(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "गुल्लक में कुल बचत" else "Total Savings"
    fun addExpense(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "खर्च जोड़ें" else "Add Expense"
    fun addIncome(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "आय जोड़ें" else "Add Income"
    fun recentTransactions(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "हालिया लेन-देन" else "Recent Transactions"
    fun viewAll(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "सभी देखें" else "View All"
    fun noTransactionsYet(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "अभी कोई लेन-देन दर्ज नहीं है" else "No transactions recorded yet"
    fun addFirstTransaction(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "+ बटन दबाकर आज का पहला खर्च या आय जोड़ें" else "Tap + to add your first expense or income"

    // Transaction Details & Actions
    fun income(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "आय (Income)" else "Income"
    fun expense(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "खर्च (Expense)" else "Expense"
    fun all(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "सभी" else "All"
    fun amount(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रकम (₹)" else "Amount (₹)"
    fun category(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "श्रेणी (Category)" else "Category"
    fun date(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "तारीख" else "Date"
    fun note(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "विवरण / नोट" else "Note / Details"
    fun paymentMode(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "भुगतान का माध्यम" else "Payment Mode"
    fun cash(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "नकद (Cash)" else "Cash"
    fun upi(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "UPI / GooglePay" else "UPI / Online"
    fun bank(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बैंक खाता" else "Bank Account"
    fun save(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "सुरक्षित करें" else "Save"
    fun cancel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रद्द करें" else "Cancel"
    fun delete(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "हटाएं" else "Delete"
    fun edit(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बदलाव करें" else "Edit"
    fun confirmDelete(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "क्या आप इसे हटाना चाहते हैं?" else "Are you sure you want to delete this?"
    fun searchPlaceholder(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "राशन, दूध, बिल आदि खोजें..." else "Search grocery, milk, bills..."

    // Dates
    fun today(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "आज" else "Today"
    fun yesterday(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कल" else "Yesterday"
    fun thisWeek(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "इस हफ्ते" else "This Week"
    fun thisMonth(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "इस महीने" else "This Month"

    // Budget
    fun budgetTitle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "महीने का बजट" else "Monthly Budget"
    fun totalMonthlyBudget(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पूरे महीने का कुल बजट" else "Overall Monthly Budget"
    fun setOverallBudget(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कुल बजट तय करें" else "Set Overall Budget"
    fun setCategoryBudget(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "श्रेणी बजट तय करें" else "Set Category Budget"
    fun spent(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "खर्च हुआ" else "Spent"
    fun remainingBudget(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बचा बजट" else "Remaining"
    fun overBudget(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बजट से अधिक खर्च!" else "Over Budget!"
    fun budgetSafe(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बजट नियंत्रण में है" else "Budget is on track"
    fun categoryWiseBudget(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "श्रेणी के अनुसार बजट" else "Category Budgets"
    fun noCategoryBudgetYet(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "राशन, दूध, बिल आदि के लिए बजट सीमा तय करें" else "Set budget limits for grocery, milk, bills, etc."

    // Savings Goals
    fun savingsTitle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "माँ की गुल्लक (बचत लक्ष्य)" else "Savings Goals (Gullak)"
    fun addSavingsGoal(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "नया बचत लक्ष्य जोड़ें" else "Add New Savings Goal"
    fun goalName(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लक्ष्य का नाम (जैसे: दीवाली, सोने के कंगन)" else "Goal Title (e.g., Diwali, Gold Savings)"
    fun targetAmount(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लक्ष्य राशि (₹)" else "Target Amount (₹)"
    fun savedAmount(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "अभी तक जमा राशि (₹)" else "Saved Amount (₹)"
    fun depositMoney(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पैसे जमा करें" else "Deposit Money"
    fun withdrawMoney(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पैसे निकालें" else "Withdraw Money"
    fun goalAchieved(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बधाई! लक्ष्य पूरा हुआ 🎉" else "Congratulations! Goal Reached 🎉"
    fun progress(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "प्रगति" else "Progress"
    fun emptySavingsPrompt(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "भविष्य की खुशियों के लिए गुल्लक में बचत का लक्ष्य बनाएं" else "Start a savings goal for family occasions or dreams"

    // Reports & Analytics
    fun reportsTitle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "हिसाब-किताब रिपोर्ट" else "Financial Reports"
    fun incomeVsExpense(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "आय बनाम खर्च" else "Income vs Expenses"
    fun expenseBreakdown(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "खर्च का विवरण (कहाँ कितना गया)" else "Expense Category Breakdown"
    fun exportCsv(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रिपोर्ट शेयर / एक्सेल (CSV)" else "Export & Share Report (CSV)"
    fun netSavings(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कुल बचत (Income - Expense)" else "Net Savings"

    // Settings & Security
    fun settings(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "सेटिंग्स (Settings)" else "Settings"
    fun language(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "भाषा चुनें / Language" else "Language"
    fun pinLock(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "सुरक्षा पिन (PIN Lock)" else "Security PIN Lock"
    fun pinEnabledDesc(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "ऐप खोलने पर 4 अंकों का पिन पूछा जाएगा" else "Ask 4-digit PIN on opening app"
    fun setPin(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "नया पिन बनाएं" else "Set PIN"
    fun changePin(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पिन बदलें" else "Change PIN"
    fun disablePin(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पिन बंद करें" else "Disable PIN"
    fun enterPin(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "अपना 4 अंकों का पिन दर्ज करें" else "Enter your 4-digit PIN"
    fun enterNewPin(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "नया 4 अंकों का पिन दर्ज करें" else "Enter new 4-digit PIN"
    fun wrongPin(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "गलत पिन! कृपया दोबारा प्रयास करें" else "Incorrect PIN! Try again"
    fun pinDisabledSuccess(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पिन सुरक्षा बंद कर दी गई" else "PIN lock disabled"
    fun pinSetSuccess(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पिन सफलतापूर्वक सुरक्षित हो गया" else "PIN set successfully"
    fun backupData(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "डेटा बैकअप लें (Save Backup)" else "Backup Data"
    fun restoreData(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बैकअप वापस लाएं (Restore)" else "Restore Data"
    fun loadSampleData(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "उदाहरण डेटा भरें (Demo Data)" else "Load Sample Demo Data"
    fun sampleDataLoaded(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "उदाहरण हिसाब लोड हो गया (Starting ₹2,000, Grocery ₹500, Milk ₹100, Electricity ₹300)" else "Sample data loaded successfully"
    fun clearAllData(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "सभी डेटा साफ़ करें" else "Reset All Data"
    fun clearDataWarning(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "क्या आप सभी हिसाब और लक्ष्य हटाना चाहते हैं?" else "Are you sure you want to delete all entries?"
    fun backupExportSuccess(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "बैकअप फ़ाइल तैयार है" else "Backup file generated"
    fun dataRestoredSuccess(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "डेटा सफलतापूर्वक रीस्टोर हो गया" else "Data restored successfully"
    fun quickAmounts(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "त्वरित रकम जोड़ें:" else "Quick Add Amount:"
}
