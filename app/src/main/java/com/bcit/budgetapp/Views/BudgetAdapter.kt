package com.bcit.budgetapp.Views

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bcit.budgetapp.Models.Budget
import com.bcit.budgetapp.R


class BudgetAdapter(private var mList: ArrayList<Budget>?) :
    RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {

    // Holds the views
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView_type: TextView = itemView.findViewById(R.id.textView_budgetFragment_type)
        val textView_amount: TextView = itemView.findViewById(R.id.textView_budgetFragment_bAmount)
//        @SuppressLint("ResourceType")
        val imageView: ImageView = itemView.findViewById(R.id.imageView_budget)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_budgetcat_budgetfragment, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        holder.textView_type.text = mList!![position].category.toString()
        holder.textView_amount.text = mList!![position].amount.toString()
        holder.imageView.setImageResource(R.drawable.budget)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList!!.size
    }

    fun update(budgetList:ArrayList<Budget>){
        mList = budgetList
        notifyDataSetChanged()
    }

}