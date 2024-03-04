package com.bcit.budgetapp.Views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcit.budgetapp.R
import com.bcit.budgetapp.Models.Transaction
import com.bcit.budgetapp.Models.TransactionCategory
import com.bcit.budgetapp.ViewModels.BudgetViewModel
import com.bcit.budgetapp.Views.MainFragments.SortType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase


class transaction_recycler(private var mList: List<Transaction?>) :
    RecyclerView.Adapter<transaction_recycler.ViewHolder>(), sortFilterRecycler
{
    val viewModel = BudgetViewModel()

    // Holds the views
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView)
    {
        //val textView: TextView = itemView.findViewById(R.id.textView) 
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_billfragment, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.itemView.findViewById<TextView>(R.id.budget_type).text = mList[position]?.category!!.name
        holder.itemView.findViewById<TextView>(R.id.budget_due).text = mList[position]?.date.toString()
        holder.itemView.findViewById<TextView>(R.id.budget_amount).text = mList[position]?.amount.toString()

    }

    // return the number of the items in the list
    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun sort(sortType: SortType)
    {
        mList = when(sortType)
        {
            SortType.LEAST_RECENT -> mList.sortedBy {it?.date}
            SortType.MOST_RECENT -> mList.sortedByDescending { it?.date }
        }

        notifyDataSetChanged()
    }

    override fun filter(filterType: TransactionCategory, sortType: SortType, budgetViewModel: BudgetViewModel)
    {
        if(budgetViewModel.allTransaction.value != null)
        {
            budgetViewModel.updateTransactions()
            mList = if (filterType != TransactionCategory.NONE)
            {
                budgetViewModel.allTransaction.value!!.filter { it?.category == filterType }
            } else
            {
                budgetViewModel.allTransaction.value!!
            }

            sort(sortType)
        }
        else
        {
            mList = listOf()
        }

    }

}