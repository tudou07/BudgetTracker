package com.bcit.budgetapp.Models

import com.google.firebase.firestore.Exclude

class Budget(val userUniqueID: String? = null, val amount: Double? = null, val category: TransactionCategory?=null)
{
    @get: Exclude
    var id: String? = null

    override fun equals(o: Any?): Boolean {
        if (o !is Budget){
            return false
        }

        return (userUniqueID == o.userUniqueID && category == o.category)
    }

}