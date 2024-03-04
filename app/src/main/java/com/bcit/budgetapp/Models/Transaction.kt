package com.bcit.budgetapp.Models

import java.util.*

enum class TransactionCategory
{
    NONE,
    ENTERTAINMENT,
    GROCERIES,
    TRANSPORTATION,
    CLOTHING,
    BILLS,
    ELECTRICITY,
    HOUSING,
    WATER,
    PHONE,
    GAS
}

open class Transaction(val userUniqueID: String? = null,
                       val amount: Double? = null,
                       val date: Date? = null,
                       val category: TransactionCategory? = null)
{
}