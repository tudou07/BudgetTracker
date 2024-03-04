package com.bcit.budgetapp.Views.MainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bcit.budgetapp.Models.Budget
import com.bcit.budgetapp.Models.TransactionCategory
import com.bcit.budgetapp.R
import com.bcit.budgetapp.ViewModels.BudgetViewModel
import com.bcit.budgetapp.Views.budget_recycler
import com.bcit.budgetapp.databinding.FragmentBillBinding
import com.bcit.budgetapp.databinding.FragmentProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment()
{
    private val budgetViewModel: BudgetViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewProfileBudgetCategory.adapter =
            budgetViewModel.budgets.value?.let { budget -> budget_recycler(budget.filter { it.category != TransactionCategory.NONE }, budgetViewModel) }
        binding.recyclerViewProfileBudgetCategory.layoutManager = LinearLayoutManager(activity)

        binding.textViewProfileBudgetAmount.text = budgetViewModel.getTotalBudget().toString()
        //binding.textViewProfileBudgetAmountSpent.text = budgetViewModel.getTotalSpent().toString()

        var spentObserver = Observer<Boolean> { _ ->
            binding.textViewProfileBudgetAmountSpent.text = budgetViewModel.getTotalSpent().toString()
            binding.textViewProfileBudgetAmount.text = budgetViewModel.getTotalBudget().toString()
        }

        var budgetObserver = Observer<ArrayList<Budget>> { budgetList ->
            val budgets = budgetList.filter { it.category != TransactionCategory.NONE }
            (binding.recyclerViewProfileBudgetCategory.adapter as budget_recycler).update(budgets as ArrayList<Budget>)
            binding.textViewProfileBudgetAmount.text = budgetViewModel.getTotalBudget().toString()
        }

        budgetViewModel.budgets.observe(viewLifecycleOwner, budgetObserver)
        budgetViewModel.dateGot.observe(viewLifecycleOwner, spentObserver)
        if(budgetViewModel.dateGot.value == true)
        {
            binding.textViewProfileBudgetAmount.text = budgetViewModel.getTotalBudget().toString()
        }
    }

}