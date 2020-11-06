package com.misiak.android.autoexpense.carinformation.fuelexpenses

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentSaveOrUpdateCarBinding
import com.misiak.android.autoexpense.databinding.FragmentSaveOrUpdateFuelExpenseBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.Action
import com.misiak.android.autoexpense.repository.FuelExpenseRepository
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.properties.Delegates

class SaveOrUpdateFuelExpenseFragment : Fragment() {

    private lateinit var binding: FragmentSaveOrUpdateFuelExpenseBinding
    private lateinit var viewModel: SaveOrUpdateFuelExpenseViewModel
    private var carId by Delegates.notNull<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_save_or_update_fuel_expense, container, false)
        val account = SaveOrUpdateFuelExpenseFragmentArgs.fromBundle(requireArguments()).account
        val fuelExpenseId = SaveOrUpdateFuelExpenseFragmentArgs.fromBundle(requireArguments()).fuelExpenseId
        val actionType = SaveOrUpdateFuelExpenseFragmentArgs.fromBundle(requireArguments()).action
        carId = SaveOrUpdateFuelExpenseFragmentArgs.fromBundle(requireArguments()).carId
        val database = getDatabase(requireContext())
        val repository = FuelExpenseRepository(database, account)
        val viewModelFactory = SaveOrUpdateFuelExpenseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SaveOrUpdateFuelExpenseViewModel::class.java)

        if (actionType == Action.UPDATE)
            editFuelExpense(fuelExpenseId)

        binding.saveButton.setOnClickListener {
            if (isDataValid()) {
                hideKeyboard()
                val fuelExpense = extractFuelExpense()
                if (actionType == Action.UPDATE)
                    println(fuelExpense)
            }
        }

        binding.editFuelExpenseCard.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }

    private fun editFuelExpense(fuelExpenseId: Long) {
        viewModel.editFuelExpense(fuelExpenseId)

        viewModel.fuelExpenseToSave?.observe(viewLifecycleOwner, Observer { fuelExpense ->
            fuelExpense?.let {
                fillFuelExpenseValuesToUpdateScreen(it)
            }
        })
    }

    private fun fillFuelExpenseValuesToUpdateScreen(fuelExpense: FuelExpense) {
        binding.priceText.setText(fuelExpense.price.toString())
        binding.litresText.setText(fuelExpense.litres.toString())
        binding.carMileageText.setText(fuelExpense.carMileageAfterRefuel.toString())
        binding.fuelExpenseDateText.setText(formatDateToString(fuelExpense.expenseDate))
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDateToString(date: Date): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return dateFormat.format(date)
    }

    private fun isDataValid(): Boolean {
        return true
        //TODO("Validate fields")
    }

    private fun extractFuelExpense(): FuelExpense {
        return FuelExpense(
            fuelExpenseId = viewModel.fuelExpenseToSave?.value?.fuelExpenseId ?: 0,
            price = binding.priceText.text.toString().toDouble(),
            litres = binding.litresText.text.toString().toDouble(),
            carMileageAfterRefuel = binding.carMileageText.text.toString().toDouble(),
            carId = viewModel.fuelExpenseToSave?.value?.carId ?: carId,
            expenseDate = extractDateFromString(binding.fuelExpenseDateText.text.toString()),
            averageCost = 0.0,
            averageConsumption = 0.0
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun extractDateFromString(date: String): Date {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return dateFormat.parse(date)!!
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}