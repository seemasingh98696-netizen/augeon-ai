package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    val category: String, // e.g. "GROCERY", "MILK", "SALARY", etc.
    val dateMillis: Long,
    val note: String = "",
    val paymentMode: String = "CASH" // "CASH", "UPI", "BANK"
)
