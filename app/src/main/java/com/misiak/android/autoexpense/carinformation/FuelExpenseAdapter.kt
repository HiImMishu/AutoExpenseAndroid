package com.misiak.android.autoexpense.carinformation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.databinding.ListItemFuelExpenseBinding

class FuelExpenseAdapter :
    ListAdapter<FuelExpense, FuelExpenseAdapter.ViewHolder>(FuelExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(private val binding: ListItemFuelExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FuelExpense) {
            binding.fuelExpense = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemFuelExpenseBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class FuelExpenseDiffCallback : DiffUtil.ItemCallback<FuelExpense>() {

    override fun areItemsTheSame(oldItem: FuelExpense, newItem: FuelExpense): Boolean {
        return oldItem.fuelExpenseId == newItem.fuelExpenseId
    }

    override fun areContentsTheSame(oldItem: FuelExpense, newItem: FuelExpense): Boolean {
        return oldItem == newItem
    }
}