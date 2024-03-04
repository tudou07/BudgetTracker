package com.bcit.budgetapp.Models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository {
    //Database
    var db: FirebaseFirestore = Firebase.firestore
    var firebaseAuth = FirebaseAuth.getInstance().currentUser

    fun addTransaction(transaction: Transaction){
        db.collection("Transaction")
            .add(transaction)
            .addOnSuccessListener { documentReference ->
                Log.d("ADD TRANSACTION", "Added with ${documentReference.id}")
            }
            .addOnFailureListener{ e ->
                Log.w("ADD TRANSACTION", "Failed with ", e)
            }
    }

    fun getTransactionFlow(): Flow<List<Transaction?>> {
        return db.collection("Transaction")
            .snapshots().map { snapshot ->
                snapshot.documents.filter { document ->
                    document.contains("userUniqueID")
                            && document.getString("userUniqueID") == firebaseAuth?.email
                }.map { document ->
                    document.toObject<Transaction>()
                    }
            }
    }
}