package com.misiak.android.autoexpense.mainscreen

import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.view.CarWithLastFuelExpenseView
import com.misiak.android.autoexpense.databinding.ListItemCarBinding
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class CarAdapter(private val clickListener: CarClickListener) :
    ListAdapter<CarWithLastFuelExpenseView, CarAdapter.ViewHolder>(CarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarAdapter.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CarAdapter.ViewHolder, position: Int) {
        val car = getItem(position)
        holder.bind(clickListener, car)
    }

    class ViewHolder private constructor(val binding: ListItemCarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: CarClickListener, item: CarWithLastFuelExpenseView) {
            binding.carWithFuelExpense = item
            binding.clickListener = clickListener
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

class CarDiffCallback : DiffUtil.ItemCallback<CarWithLastFuelExpenseView>() {

    override fun areItemsTheSame(
        oldItem: CarWithLastFuelExpenseView,
        newItem: CarWithLastFuelExpenseView
    ): Boolean {
        return oldItem.car.id == newItem.car.id
    }

    override fun areContentsTheSame(
        oldItem: CarWithLastFuelExpenseView,
        newItem: CarWithLastFuelExpenseView
    ): Boolean {
        return oldItem == newItem
    }
}

class CarClickListener(val actionListener: (carId: Long, action: Int) -> Unit) {
    fun onClick(car: Car, action: Int) = actionListener(car.id, action)
}

class CarItemTouchHelper: ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = 0
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val binding = (viewHolder as CarAdapter.ViewHolder).binding
        when(direction) {
            ItemTouchHelper.LEFT ->
                binding.clickListener!!.onClick(binding.carWithFuelExpense!!.car, ItemTouchHelper.LEFT)

            ItemTouchHelper.RIGHT ->
                binding.clickListener!!.onClick(binding.carWithFuelExpense!!.car, ItemTouchHelper.RIGHT)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftBackgroundColor(ContextCompat.getColor(recyclerView.context, R.color.colorRed))
            .addSwipeLeftLabel("Delete ")
            .addSwipeLeftActionIcon(R.drawable.delete_icon)
            .addSwipeRightBackgroundColor(ContextCompat.getColor(recyclerView.context, R.color.colorBlue))
            .addSwipeRightLabel("Edit ")
            .addSwipeRightActionIcon(R.drawable.edit_icon)
            .setSwipeLeftLabelColor(Color.WHITE)
            .setSwipeRightLabelColor(Color.WHITE)
            .create()
            .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
    }
}