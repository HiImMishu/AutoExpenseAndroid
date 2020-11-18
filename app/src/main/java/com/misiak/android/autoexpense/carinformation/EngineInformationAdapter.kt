package com.misiak.android.autoexpense.carinformation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.databinding.EngineItemBinding

class EngineInformationAdapter(private val engine: Engine?, private val listener: EngineActionListener) : BaseExpandableListAdapter() {


    override fun getGroup(groupPosition: Int): Any {
        return "Engine Information"
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    @SuppressLint("InflateParams")
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        return convertView ?: LayoutInflater.from(parent!!.context)
            .inflate(R.layout.engine_label_layout, null)
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Engine? {
        return engine
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val layoutInflater = LayoutInflater.from(parent!!.context)
        val binding = EngineItemBinding.inflate(layoutInflater, parent, false)
        binding.engine = engine
        binding.actionListener = listener
        return binding.root
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return 1
    }
}

class EngineActionListener(private val listener: (engineId: Long) -> Unit) {
    fun onEditClicked(engineId: Long) = listener(engineId)
}