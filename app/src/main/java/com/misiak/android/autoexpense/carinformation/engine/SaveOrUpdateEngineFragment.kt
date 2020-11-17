package com.misiak.android.autoexpense.carinformation.engine

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentSaveOrUpdateEngineBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.Action
import com.misiak.android.autoexpense.repository.EngineRepository

class SaveOrUpdateEngineFragment : Fragment() {

    private lateinit var binding: FragmentSaveOrUpdateEngineBinding
    private lateinit var viewModel: SaveOrUpdateEngineViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_save_or_update_engine, container, false)
        val account = SaveOrUpdateEngineFragmentArgs.fromBundle(requireArguments()).account
        val action = SaveOrUpdateEngineFragmentArgs.fromBundle(requireArguments()).action
        val carId = SaveOrUpdateEngineFragmentArgs.fromBundle(requireArguments()).carId
        val engineId = SaveOrUpdateEngineFragmentArgs.fromBundle(requireArguments()).engineId
        val database = getDatabase(requireContext())
        val repository = EngineRepository(database, account)
        val viewModelFactory = SaveOrUpdateEngineViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SaveOrUpdateEngineViewModel::class.java)

        if (action == Action.UPDATE)
            editEngine(engineId)

        viewModel.updateOrSaveCompleted.observe(viewLifecycleOwner, Observer {
            if (it) {
                parentFragmentManager.popBackStack()
                viewModel.updateOrSaveOperationHandled()
            }
        })

        binding.saveButton.setOnClickListener {
            if (isDataValid()) {
                hideKeyboard()
                val engine = extractEngine(carId)
                if (action == Action.UPDATE)
                    viewModel.updateEngine(engine)
                else
                    viewModel.saveEngine(engine)
            }
        }

        binding.editEngineCard.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }

    private fun editEngine(engineId: Long) {
        viewModel.getEngineFromDatabase(engineId)

        viewModel.engineToSave?.observe(viewLifecycleOwner, Observer { engine ->
            engine?.let {
                fillEngineValuesToUpdateScree(it)
            }
        })
    }

    private fun extractEngine(carId: Long): Engine {
        return Engine(
            id = viewModel.engineToSave?.value?.id ?: 0,
            capacity = binding.capacityText.text.toString().toDouble(),
            horsepower = binding.horsepowerText.text.toString().toDouble(),
            cylinders = binding.cylindersText.text.toString().toInt(),
            carId = carId
        )
    }

    private fun isDataValid(): Boolean {
        var errorFlag = true
        val fields = listOf(binding.capacityText, binding.horsepowerText, binding.cylindersText)

        fields.map { field ->
            if (isFieldEmpty(field))
                errorFlag = false
        }

        return errorFlag
    }

    private fun isFieldEmpty(field: EditText): Boolean {
        return if (field.text.toString().isEmpty()) {
            field.error = requireActivity().getString(R.string.empty_field_error)
            true
        } else
            false
    }

    private fun fillEngineValuesToUpdateScree(engine: Engine) {
        binding.capacityText.setText(engine.capacity.toString())
        binding.horsepowerText.setText(engine.horsepower.toString())
        binding.cylindersText.setText(engine.cylinders.toString())
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}