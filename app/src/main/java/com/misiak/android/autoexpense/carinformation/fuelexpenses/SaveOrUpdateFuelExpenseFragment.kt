package com.misiak.android.autoexpense.carinformation.fuelexpenses

import android.annotation.SuppressLint
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.authentication.SignInFragment
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentSaveOrUpdateFuelExpenseBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.Action
import com.misiak.android.autoexpense.repository.FuelExpenseRepository
import kotlinx.android.synthetic.main.fragment_save_or_update_car.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class SaveOrUpdateFuelExpenseFragment : Fragment() {

    private lateinit var binding: FragmentSaveOrUpdateFuelExpenseBinding
    private lateinit var viewModel: SaveOrUpdateFuelExpenseViewModel
    private lateinit var repository: FuelExpenseRepository
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
        repository = FuelExpenseRepository(database, account)
        val viewModelFactory = SaveOrUpdateFuelExpenseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SaveOrUpdateFuelExpenseViewModel::class.java)

        if (actionType == Action.UPDATE)
            editFuelExpense(fuelExpenseId)
        else
            addActualDate()

        setUpSuccessListener()
        setUpSaveButtonListener(actionType)
        setUpHideKeyboardOnBackgroundClickListener()
        setUpConnectionErrorListener()
        setUpTokenExpirationListener()
        setUpOperationFailedListener()

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

    private fun addActualDate() {
        binding.fuelExpenseDateText.setText(formatDateToString(Date(System.currentTimeMillis())))
    }

    private fun setUpSuccessListener() {
        viewModel.updateOrSaveCompleted.observe(viewLifecycleOwner, Observer { statusActive ->
            if (statusActive) {
                parentFragmentManager.popBackStack()
                viewModel.updateOrSaveOperationHandled()
            }
        })
    }

    private fun setUpSaveButtonListener(actionType: Action) {
        binding.saveButton.setOnClickListener {
            if (isDataValid()) {
                hideKeyboard()
                val fuelExpense = extractFuelExpense()
                if (actionType == Action.UPDATE)
                    viewModel.updateFuelExpense(fuelExpense)
                else
                    viewModel.saveFuelExpense(fuelExpense)
            }
        }
    }

    private fun isDataValid(): Boolean {
        var errorFlag = true
        val fields = listOf(binding.priceText, binding.litresText, binding.carMileageText, binding.fuelExpenseDateText)

        fields.map { field ->
            if (isFieldEmpty(field))
                errorFlag = false
        }

        if (!isDateValid())
            errorFlag = false

        return errorFlag
    }

    private fun isFieldEmpty(field: EditText): Boolean {
        return if (field.text.toString().isEmpty()) {
            field.error = requireActivity().getString(R.string.empty_field_error)
            true
        } else
            false
    }

    private fun isDateValid(): Boolean {
        try {
            extractDateFromString(binding.fuelExpenseDateText.text.toString())
        } catch (e: ParseException) {
            binding.fuelExpenseDateText.error = requireActivity().getString(R.string.invalid_date_error)
            return false
        }
        return true
    }

    @SuppressLint("SimpleDateFormat")
    private fun extractDateFromString(date: String): Date {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        return dateFormat.parse(date)!!
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

    private fun setUpHideKeyboardOnBackgroundClickListener() {
        binding.editFuelExpenseCard.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun setUpConnectionErrorListener() {
        viewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar(requireActivity().getString(R.string.internet_connection_error))
                viewModel.connectionErrorHandled()
            }
        })
    }

    private fun setUpTokenExpirationListener() {
        viewModel.tokenExpired.observe(viewLifecycleOwner, Observer { tokenIsExpired ->
            if (tokenIsExpired) {
                SignInFragment.getAccount(requireContext()) { account ->
                    updateRepositoryAccount(account)
                }
            }
        })
    }

    private fun updateRepositoryAccount(account: GoogleSignInAccount) {
        repository.account = account
        viewModel.tokenRefreshed()
        binding.editFuelExpenseCard.saveButton.performClick()
    }

    private fun setUpOperationFailedListener() {
        viewModel.unknownError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar(requireActivity().getString(R.string.operation_failed_error))
                viewModel.unknownErrorHandled()
            }
        })
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}