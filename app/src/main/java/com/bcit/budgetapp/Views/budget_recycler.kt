package com.bcit.budgetapp.Views

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcit.budgetapp.Models.Bill
import com.bcit.budgetapp.Models.Budget
import com.bcit.budgetapp.Models.TransactionCategory
import com.bcit.budgetapp.R
import com.bcit.budgetapp.ViewModels.BudgetViewModel
import com.bcit.budgetapp.Views.MainFragments.SortType


class budget_recycler(private var mList: List<Budget>, private val budgetViewModel: BudgetViewModel) :
    RecyclerView.Adapter<budget_recycler.ViewHolder>()
{
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
            .inflate(R.layout.recyclerview_item_profilefragment, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        holder.itemView.findViewById<TextView>(R.id.budget_cat).text = mList[position].category?.name
        holder.itemView.findViewById<TextView>(R.id.budget_limit).text = mList[position].amount.toString()
        holder.itemView.findViewById<TextView>(R.id.budget_spent).text =
            mList[position].category?.let { budgetViewModel.getTotalSpent(it).toString() }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int
    {
        return mList.size
    }

    fun update(budgetList:ArrayList<Budget>){
        mList = budgetList
        notifyDataSetChanged()
    }
}