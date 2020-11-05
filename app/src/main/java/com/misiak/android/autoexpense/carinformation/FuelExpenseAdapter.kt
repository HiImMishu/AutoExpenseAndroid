package com.misiak.android.autoexpense.carinformation

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
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.databinding.ListItemFuelExpenseBinding
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class FuelExpenseAdapter(private val actionListener: FuelExpenseActionListener) :
    ListAdapter<FuelExpense, FuelExpenseAdapter.ViewHolder>(FuelExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fuelExpense = getItem(position)
        holder.bind(actionListener, fuelExpense)
    }

    class ViewHolder private constructor(val binding: ListItemFuelExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(actionListener: FuelExpenseActionListener, item: FuelExpense) {
            binding.fuelExpense = item
            binding.actionListener = actionListener
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

class FuelExpenseActionListener(val actionListener: (fuelExpenseId: Long, action: Int) -> Unit) {
    fun onAction(fuelExpenseId: Long, action: Int) = actionListener(fuelExpenseId, action)
}

class FuelExpenseItemTouchHelper: ItemTouchHelper.Callback() {

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
        val binding = (viewHolder as FuelExpenseAdapter.ViewHolder).binding

        when (direction) {
            ItemTouchHelper.LEFT ->
                binding.actionListener!!.onAction(binding.fuelExpense!!.fuelExpenseId, ItemTouchHelper.LEFT)
            ItemTouchHelper.RIGHT ->
                binding.actionListener!!.onAction(binding.fuelExpense!!.fuelExpenseId, ItemTouchHelper.RIGHT)
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
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
    }
}