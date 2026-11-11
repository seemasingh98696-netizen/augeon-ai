package com.example.util

import java.util.Locale

object CurrencyFormatter {

    /**
     * Formats an amount with the Indian Rupee symbol (₹) using Indian numbering system
     * e.g. ₹2,000, ₹1,25,000, ₹500
     */
    fun formatRupee(amount: Double, showDecimals: Boolean = false): String {
        val isNegative = amount < 0
        val absAmount = kotlin.math.abs(amount)
        val longVal = absAmount.toLong()
        val decimalVal = absAmount - longVal

        val formattedInt = formatIndianInteger(longVal)
        val resultStr = if (showDecimals && decimalVal > 0.001) {
            val decStr = String.format(Locale.US, "%.2f", decimalVal).substring(2)
            "$formattedInt.$decStr"
        } else {
            formattedInt
        }

        return if (isNegative) "-₹$resultStr" else "₹$resultStr"
    }

    private fun formatIndianInteger(num: Long): String {
        val s = num.toString()
        val len = s.length
        if (len <= 3) return s

        val lastThree = s.substring(len - 3)
        val remaining = s.substring(0, len - 3)

        val sb = StringBuilder()
        var count = 0
        for (i in remaining.length - 1 downTo 0) {
            sb.append(remaining[i])
            count++
            if (count % 2 == 0 && i > 0) {
                sb.append(',')
            }
        }
        return sb.reverse().toString() + "," + lastThree
    }

    /**
     * Formats rupee with compact representation (e.g. ₹25k or ₹1.5L)
     */
    fun formatCompactRupee(amount: Double): String {
        val abs = kotlin.math.abs(amount)
        val sign = if (amount < 0) "-" else ""
        return when {
            abs >= 10000000 -> "$sign₹${String.format(Locale.US, "%.1f", abs / 10000000)}Cr"
            abs >= 100000 -> "$sign₹${String.format(Locale.US, "%.1f", abs / 100000)}L"
            abs >= 1000 -> "$sign₹${String.format(Locale.US, "%.1f", abs / 1000)}k"
            else -> "$sign₹${abs.toInt()}"
        }
    }
}
