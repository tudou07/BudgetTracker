package com.bcit.budgetapp.ViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bcit.budgetapp.Models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class BudgetViewModel : ViewModel()
{
    val userID: String = "sahilrai"
    val transactions = ArrayList<Transaction>()
    var db: FirebaseFirestore = Firebase.firestore
    var user = FirebaseAuth.getInstance().currentUser

    //Live Data
    val allTransaction: MutableLiveData<List<Transaction?>> = MutableLiveData<List<Transaction?>>()
    val allBills: MutableLiveData<List<Bill?>> = MutableLiveData<List<Bill?>>()
    val budgets: MutableLiveData<ArrayList<Budget>> = MutableLiveData<ArrayList<Budget>>()

    private val budgetRepository = BudgetRepository()
    private val transactionRepository = TransactionRepository()
    private val billRepository = BillRepository()

    private lateinit var lastDate : Date
    private lateinit var dateId : String
    public var dateGot: MutableLiveData<Boolean> = MutableLiveData()
    init{
        getLastAccessForUser(userID)
        observeTransactions()
        observerBills()
        loadBudgets()
    }

    private fun getLastAccessForUser(userUniqueID: String) {
        db.collection("LastAccessDates")
            .whereEqualTo("userUniqueID", userUniqueID)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty)
                {
                    addLastAccessForUser(userUniqueID)
                }
                else
                {
                    lastDate = documents.first().toObject(LastAccessDate::class.java).date!!
                    dateId = documents.first().id
                    dateGot.value = true
                }

            }
            .addOnFailureListener { e ->
                addLastAccessForUser(userUniqueID)
            }
    }

    private fun addLastAccessForUser(userUniqueID: String){
        val lastAccessDate = LastAccessDate(userUniqueID, Date.from(Instant.now()))
        db.collection("LastAccessDates")
            .add(lastAccessDate)
            .addOnSuccessListener { documentReference ->
                Log.d("ADD date", "Added with ${documentReference.id}")
                lastDate = lastAccessDate.date!!
                dateId = documentReference.id
                dateGot.value = true
            }
            .addOnFailureListener{ e ->
                Log.w("ADD date", "Failed with ", e)
            }
    }

    private fun updateLastAccessForUser(userUniqueID: String) {
        val date = Date.from(Instant.now())
        db.collection("LastAccessDates")
            .document(dateId)
            .update("date", date)
            .addOnSuccessListener {
                lastDate = date
            }
            .addOnFailureListener{ e ->
                Log.w("UPDATE dateT", "Failed with ", e)
            }
    }

    fun addTransaction(transaction: Transaction)
    {
        transactionRepository.addTransaction(transaction)
        transactions.add(transaction)
    }

    fun addBill(bill: Bill)
    {
        billRepository.addBill(bill)
    }

    fun addOrUpdateBudget(budget: Budget){
        val matchingBudget = budgets.value!!.firstOrNull { it == budget }
        if (matchingBudget != null){
            budget.id = matchingBudget.id
            updateBudget(budget)
        } else {
            addBudget(budget)
        }
    }

    private fun addBudget(budget: Budget){
        budgetRepository.addBudget(budget) { savedBudget ->
            budgets.value?.add(savedBudget)
            budgets.value = budgets.value
        }
    }

    private fun updateBudget(budget: Budget){
        budgetRepository.updateBudget(budget) { updatedBudget ->
            budgets.value?.remove(budget)
            budgets.value?.add(updatedBudget)
            budgets.value = budgets.value
        }
    }

    public fun getTotalSpent(transactionCategory: TransactionCategory = TransactionCategory.NONE): Double
    {
        updateTransactions()
        var total = 0.0
        val currentDate = Date.from(Instant.now())

        for(transaction: Transaction? in allTransaction.value!!)
        {
            if(transaction!!.date?.month == currentDate.month && transaction.date!!.year == currentDate.year)
            {
                if (transactionCategory == TransactionCategory.NONE) // give total of all
                {
                    total += transaction.amount!!
                } else if (transactionCategory == TransactionCategory.BILLS) // give total of bills only
                {
                    if (transaction.category!! > TransactionCategory.BILLS)
                    {
                        total += transaction.amount!!
                    }
                } else if (transaction.category == transactionCategory) //give total of specified category
                {
                    total += transaction.amount!!
                }
            }
        }

        return total
    }

    fun getTotalBudget(): Double {
        var totalBudget = 0.0


        for (budget: Budget in budgets.value!!){
            totalBudget += budget.amount!!
        }
        return totalBudget
    }

    private fun observeTransactions(){
        val scope = CoroutineScope(Dispatchers.Main)

        scope.launch {
            transactionRepository.getTransactionFlow().collect{
                allTransaction.value = it
            }
        }
    }

    private fun observerBills(){
        val scope = CoroutineScope(Dispatchers.Main)

        scope.launch {
            billRepository.getBillsFlow().collect{
                allBills.value = it
            }
        }
    }

    private fun loadBudgets(){
        budgets.value = ArrayList()
        budgetRepository.getBudgetsForUser(userID) { budget ->
            budgets.value?.add(budget)
            budgets.value = budgets.value
        }
    }

    public fun updateTransactions()
    {
        val currentDate: Date = Date.from(Instant.now())

            for(bill in allBills.value!!)
            {
                val newDate: Date = bill?.date?.clone() as Date


                if(bill.billType == BillType.ANNUALLY)
                {
                    newDate.year = currentDate.year
                    val transaction: Transaction = Transaction(bill.userUniqueID, bill.amount, newDate, bill.category)

                    if(transaction.date!! <  currentDate && transaction.date > lastDate)
                    {
                        addTransaction(transaction)
                    }
                }
                else if(bill.billType == BillType.MONTHLY)
                {
                    newDate.year = currentDate.year
                    newDate.month = currentDate.month
                    val transaction: Transaction = Transaction(bill.userUniqueID,
                        bill.amount, newDate, bill.category)
                    if(transaction.date!! < currentDate && transaction.date > lastDate)
                    {
                        addTransaction(transaction)
                    }
                }
            }

        updateLastAccessForUser(userID)
    }
}