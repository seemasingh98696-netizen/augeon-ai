package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val icon: ImageVector,
    val color: Color
)

object CategoryConstants {
    val EXPENSE_CATEGORIES = listOf(
        CategoryItem("GROCERY", "Grocery", "राशन / किराना", Icons.Default.ShoppingCart, Color(0xFF2E7D32)),
        CategoryItem("MILK", "Milk", "दूध", Icons.Default.LocalDrink, Color(0xFF0288D1)),
        CategoryItem("ELECTRICITY", "Electricity & Bills", "बिजली और बिल", Icons.Default.Bolt, Color(0xFFF57C00)),
        CategoryItem("SCHOOL", "School & Education", "स्कूल और पढ़ाई", Icons.Default.School, Color(0xFF7B1FA2)),
        CategoryItem("MEDICAL", "Medical & Doctor", "दवाई और डॉक्टर", Icons.Default.Healing, Color(0xFFD32F2F)),
        CategoryItem("SHOPPING", "Shopping", "कपड़े और खरीदारी", Icons.Default.ShoppingBag, Color(0xFFC2185B)),
        CategoryItem("TRAVEL", "Travel & Fuel", "यात्रा और पेट्रोल", Icons.Default.DirectionsBus, Color(0xFF00796B)),
        CategoryItem("VEGETABLES", "Vegetables & Fruits", "सब्जी और फल", Icons.Default.LocalFlorist, Color(0xFF558B2F)),
        CategoryItem("OTHER", "Other", "अन्य खर्च", Icons.Default.MoreHoriz, Color(0xFF616161))
    )

    val INCOME_CATEGORIES = listOf(
        CategoryItem("SALARY", "Salary", "वेतन / पगार", Icons.Default.Work, Color(0xFF1B5E20)),
        CategoryItem("BUSINESS", "Business / Shop", "व्यापार / दुकान", Icons.Default.Store, Color(0xFF0D47A1)),
        CategoryItem("RENT", "Rent Income", "किराया", Icons.Default.AccountBalance, Color(0xFFE65100)),
        CategoryItem("PENSION", "Pension", "पेंशन", Icons.Default.AccountBalance, Color(0xFF4A148C)),
        CategoryItem("GIFT", "Gift / Shagun", "उपहार / शगुन", Icons.Default.CardGiftcard, Color(0xFFAD1457)),
        CategoryItem("OTHER", "Other Income", "अन्य आय", Icons.Default.MoreHoriz, Color(0xFF37474F))
    )

    fun getCategory(id: String, isExpense: Boolean = true): CategoryItem {
        val list = if (isExpense) EXPENSE_CATEGORIES else INCOME_CATEGORIES
        return list.find { it.id.equals(id, ignoreCase = true) }
            ?: (if (isExpense) EXPENSE_CATEGORIES.last() else INCOME_CATEGORIES.last())
    }
}
