package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val category: String, // "OVERALL" or category id like "GROERY", "MILK", etc.
    val monthlyLimit: Double
)
