package com.bcit.budgetapp.Views.MainFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bcit.budgetapp.Models.*
import com.bcit.budgetapp.ViewModels.BudgetViewModel
import com.bcit.budgetapp.R
import com.bcit.budgetapp.ViewModels.UserViewModel
import com.bcit.budgetapp.databinding.FragmentExpenseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.sql.Date
import java.time.Instant


class ExpenseFragment : Fragment()
{
    private val budgetViewModel: BudgetViewModel by activityViewModels()
    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler()
    var user = FirebaseAuth.getInstance().currentUser



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners(view)


        binding.expenseSpinnerFreq.isEnabled = false

        binding.checkBoxExpense.setOnCheckedChangeListener { view, isChecked -> onChecked(view, isChecked)}

        var text = binding.editTextNumberDecimalExpense.text
        if (text.isNotEmpty() || text.toString() != "0") {
            binding.buttonExpenseAdd.setOnClickListener{
                addExpenseButtonClick(it)
                binding.buttonExpenseAdd.text = "✔"
                handler.postDelayed({ binding.buttonExpenseAdd.text = "Submit" }, 3000L)
                showPopup()
            }
        }
    }

    private fun onChecked(view: View, isChecked: Boolean)
    {
        binding.expenseSpinnerFreq.isEnabled = isChecked

        if(isChecked)
        {
            val adapter = ArrayAdapter<TransactionCategory>(view.context, android.R.layout.simple_spinner_item, TransactionCategory.values().filter { it > TransactionCategory.BILLS })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.expenseSpinner.adapter = adapter
        }
        else
        {
            val adapter = ArrayAdapter<TransactionCategory>(view.context, android.R.layout.simple_spinner_item, TransactionCategory.values().filter { it < TransactionCategory.BILLS && it != TransactionCategory.NONE })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.expenseSpinner.adapter = adapter
        }
    }

    private fun addExpenseButtonClick(view: View)
    {
        var amount = 0.0
        val currentDate = Date.from(Instant.now())

        val date = Date(binding.datePickerExpense.year - 1900, binding.datePickerExpense.month, binding.datePickerExpense.dayOfMonth)
        date.time = currentDate.time
        if(binding.editTextNumberDecimalExpense.text.toString().isNotEmpty())
        {
            amount = binding.editTextNumberDecimalExpense.text.toString().toDouble()
        }

        val userViewModel: UserViewModel by activityViewModels()

        if(binding.checkBoxExpense.isChecked)
        {
            val bill = Bill(user?.email, amount, date, (binding.expenseSpinner.selectedItem as TransactionCategory), (binding.expenseSpinnerFreq.selectedItem as BillType))
            budgetViewModel.addBill(bill)

        }
        else
        {
            val transaction = Transaction(user?.email, amount, date, (binding.expenseSpinner.selectedItem as TransactionCategory))
            budgetViewModel.addTransaction(transaction)
        }
    }

    private fun setupSpinners(view: View)
    {
        val adapter = ArrayAdapter<TransactionCategory>(view.context, android.R.layout.simple_spinner_item, TransactionCategory.values().filter { it < TransactionCategory.BILLS && it != TransactionCategory.NONE })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.expenseSpinner.adapter = adapter

        val adapterFreq = ArrayAdapter<BillType>(view.context, android.R.layout.simple_spinner_item, BillType.values())
        adapterFreq.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.expenseSpinnerFreq.adapter = adapterFreq
    }


    // Pop up window
    @SuppressLint("MissingInflatedId")
    fun showPopup() {
        val popupView =
            LayoutInflater.from(this.context).inflate(R.layout.popup_after_adding_expense, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
        handler.postDelayed({ popupWindow.dismiss() }, 3000L)
    }
}