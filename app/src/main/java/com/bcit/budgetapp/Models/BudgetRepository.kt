package com.bcit.budgetapp.Models

import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepository {

    //Database
    var db: FirebaseFirestore = Firebase.firestore

    fun addBudget(budget: Budget, budgetCallBack: (input: Budget) -> Unit){
        db.collection("Budget")
            .add(budget)
            .addOnSuccessListener { documentReference ->
                Log.d("ADD BUDGET", "Added with ${documentReference.id}")
                budget.id = documentReference.id
                budgetCallBack(budget)
            }
            .addOnFailureListener{ e ->
                Log.w("ADD BUDGET", "Failed with ", e)
            }
    }

    fun updateBudget(budget: Budget, budgetCallBack: (input: Budget) -> Unit){
        if ( budget.id == null){
            Log.w("UPDATE BUDGET", "Failed as original budget id not found")
            return
        }
        db.collection("Budget")
            .document(budget.id!!)
            .update("amount", budget.amount)
            .addOnSuccessListener {
                Log.d("UPDATE BUDGET", "Update succeeded")
                budgetCallBack(budget)
            }
            .addOnFailureListener{ e ->
                Log.w("UPDATE BUDGET", "Failed with ", e)
            }
    }

    fun getBudgetsForUser(userUniqueID: String, budgetCallBack: (input: Budget) -> Unit) {
        db.collection("Budget")
            .whereEqualTo("userUniqueID", userUniqueID)
            .get()
            .addOnSuccessListener { documents ->
                    for (document in documents){
                        val budget = document.toObject(Budget::class.java)
                        budget.id = document.id
                        budgetCallBack(budget)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("GET BUDGET", "Failed with ", e)
            }
    }

    fun getBudgetFlow(): Flow<List<Budget>> {
        return db.collection("Budget")
            .snapshots().map {
                    value: QuerySnapshot -> value.toObjects()
            }
    }

}