package com.example

import com.example.data.model.CategoryConstants
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun rupeeFormatting_isCorrect() {
        assertEquals("₹2,000", CurrencyFormatter.formatRupee(2000.0))
        assertEquals("₹500", CurrencyFormatter.formatRupee(500.0))
        assertEquals("-₹300", CurrencyFormatter.formatRupee(-300.0))
        assertEquals("₹1,25,000", CurrencyFormatter.formatRupee(125000.0))
    }

    @Test
    fun userPromptExample_balanceCalculationIsAccurate() {
        val startingBalance = 2000.0
        val groceryExpense = 500.0
        val milkExpense = 100.0
        val electricityExpense = 300.0

        val totalExpenses = groceryExpense + milkExpense + electricityExpense
        val remainingBalance = startingBalance - totalExpenses

        assertEquals(900.0, totalExpenses, 0.001)
        assertEquals(1100.0, remainingBalance, 0.001)
        assertEquals("₹1,100", CurrencyFormatter.formatRupee(remainingBalance))
    }

    @Test
    fun bilingualStrings_supportBothLanguages() {
        assertEquals("घर का हिसाब", AppStrings.appTitle(AppLanguage.HINDI))
        assertEquals("Ghar Ka Hisab", AppStrings.appTitle(AppLanguage.ENGLISH))

        assertEquals("राशन / किराना", CategoryConstants.getCategory("GROCERY", true).nameHi)
        assertEquals("Grocery", CategoryConstants.getCategory("GROCERY", true).nameEn)
        assertEquals("दूध", CategoryConstants.getCategory("MILK", true).nameHi)
        assertEquals("Milk", CategoryConstants.getCategory("MILK", true).nameEn)
        assertEquals("बिजली और बिल", CategoryConstants.getCategory("ELECTRICITY", true).nameHi)
    }
}
