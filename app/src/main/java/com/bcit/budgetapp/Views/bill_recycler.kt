package com.bcit.budgetapp.Views

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcit.budgetapp.Models.Bill
import com.bcit.budgetapp.Models.TransactionCategory
import com.bcit.budgetapp.R
import com.bcit.budgetapp.ViewModels.BudgetViewModel
import com.bcit.budgetapp.Views.MainFragments.SortType
import com.google.firebase.auth.FirebaseAuth


class bill_recycler(private var mList: List<Bill?>) :
    RecyclerView.Adapter<bill_recycler.ViewHolder>(), sortFilterRecycler
{
    private val user = FirebaseAuth.getInstance().currentUser

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
            .inflate(R.layout.recyclerview_bill_billfragment, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.itemView.findViewById<TextView>(R.id.budget_amount).text = mList[position]?.category?.name
        holder.itemView.findViewById<TextView>(R.id.budget_due).text = mList[position]?.amount.toString()
        holder.itemView.findViewById<TextView>(R.id.budget_type).text = mList[position]?.date.toString()
        holder.itemView.findViewById<TextView>(R.id.budget_freq).text = mList[position]?.billType!!.name
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
        if(budgetViewModel.allBills.value != null)
        {
            mList = if (filterType != TransactionCategory.NONE)
            {
                budgetViewModel.allBills.value!!.filter { it?.category == filterType }
            } else
            {
                budgetViewModel.allBills.value!!
            }

            sort(sortType)
        }
        else
        {
            mList = listOf()
        }
    }
}