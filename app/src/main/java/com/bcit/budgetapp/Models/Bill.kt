package com.bcit.budgetapp.Models

import java.util.*

enum class BillType
{
    ANNUALLY,
    MONTHLY
}

class Bill(userUniqueID: String? = null,
           amount: Double? = null,
           date: Date? = null,
           category: TransactionCategory? = null,
           val billType: BillType? = null)
    : Transaction(userUniqueID, amount, date, category)
