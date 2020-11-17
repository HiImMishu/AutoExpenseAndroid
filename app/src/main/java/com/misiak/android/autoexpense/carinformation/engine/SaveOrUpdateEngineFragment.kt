package com.misiak.android.autoexpense.carinformation.engine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.databinding.FragmentSaveOrUpdateEngineBinding

class SaveOrUpdateEngineFragment : Fragment() {

    private lateinit var binding: FragmentSaveOrUpdateEngineBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_save_or_update_engine, container, false)

        return binding.root
    }

}