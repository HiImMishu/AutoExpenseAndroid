package com.misiak.android.autoexpense.mainscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.misiak.android.autoexpense.database.view.CarWithLastFuelExpenseView
import com.misiak.android.autoexpense.databinding.ListItemCarBinding

class CarAdapter: ListAdapter<CarWithLastFuelExpenseView, CarAdapter.ViewHolder>(CarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CarAdapter.ViewHolder, position: Int) {
        val car = getItem(position)
        holder.bind(car)
    }

    class ViewHolder private constructor(private val binding: ListItemCarBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CarWithLastFuelExpenseView) {
            binding.carWithFuelExpense = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCarBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CarDiffCallback: DiffUtil.ItemCallback<CarWithLastFuelExpenseView>() {

    override fun areItemsTheSame(oldItem: CarWithLastFuelExpenseView, newItem: CarWithLastFuelExpenseView): Boolean {
        return oldItem.car.id == newItem.car.id
    }

    override fun areContentsTheSame(oldItem: CarWithLastFuelExpenseView, newItem: CarWithLastFuelExpenseView): Boolean {
        return oldItem == newItem
    }
}