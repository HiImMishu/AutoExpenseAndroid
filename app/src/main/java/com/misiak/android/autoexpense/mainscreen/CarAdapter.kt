package com.misiak.android.autoexpense.mainscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.databinding.ListItemCarBinding

class CarAdapter: ListAdapter<Car, CarAdapter.ViewHolder>(CarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CarAdapter.ViewHolder, position: Int) {
        val car = getItem(position)
        holder.bind(car)
    }

    class ViewHolder private constructor(val binding: ListItemCarBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Car) {
            binding.car = item
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

class CarDiffCallback: DiffUtil.ItemCallback<Car>() {

    override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean {
        return oldItem == newItem
    }
}