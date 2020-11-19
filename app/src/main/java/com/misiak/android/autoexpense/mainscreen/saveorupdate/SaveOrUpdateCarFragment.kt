package com.misiak.android.autoexpense.mainscreen.saveorupdate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.misiak.android.autoexpense.FragmentWithOverflowMenu
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.authentication.SignInFragment
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentSaveOrUpdateCarBinding
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.android.synthetic.main.fragment_save_or_update_car.view.*

class SaveOrUpdateCarFragment : FragmentWithOverflowMenu() {

    private lateinit var binding: FragmentSaveOrUpdateCarBinding
    private lateinit var viewModel: SaveOrUpdateViewModel
    private lateinit var account: GoogleSignInAccount
    private lateinit var repository: CarRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_save_or_update_car,
            container,
            false
        )
        account = SaveOrUpdateCarFragmentArgs.fromBundle(requireArguments()).account
        val carId = SaveOrUpdateCarFragmentArgs.fromBundle(requireArguments()).carId
        val action = SaveOrUpdateCarFragmentArgs.fromBundle(requireArguments()).action
        val database = getDatabase(requireContext())
        repository = CarRepository(database, account)
        val viewModelFactory = SaveOrUpdateViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SaveOrUpdateViewModel::class.java)

        if (action == Action.UPDATE)
            editCar(carId)

        setSaveButtonListener(action)
        setUpConnectionErrorListener()
        setUpTokenExpirationListener()
        setUpOperationSuccessListener()
        setUpOperationFailedListener()
        setUpHideKeyboardOnBackgroundClickListener()

        return binding.root
    }

    private fun editCar(carId: Long) {
        viewModel.editCar(carId)

        viewModel.carToSave?.observe(viewLifecycleOwner, Observer { car ->
            car?.let {
                fillCarValuesToEditScreen(it)
            }
        })
    }

    private fun fillCarValuesToEditScreen(car: Car) {
        binding.makeText.setText(car.make)
        binding.modelText.setText(car.model)
        binding.productionYearText.setText(car.productionYear.toString())
        binding.mileageText.setText(car.mileage.toString())
        binding.purchasePriceText.setText(car.basePrice.toString())
    }

    private fun setSaveButtonListener(action: Action) {
        binding.editCarCard.saveButton.setOnClickListener {
            if (isDataValid()) {
                hideKeyboard()
                val car = extractCar()
                if (action == Action.UPDATE)
                    viewModel.updateCar(car)
                else
                    viewModel.saveCar(car)
            }
        }
    }

    private fun isDataValid(): Boolean {
        var errorFlag = true
        val fields = listOf(binding.makeText, binding.modelText, binding.productionYearText, binding.mileageText, binding.purchasePriceText)

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

    private fun extractCar(): Car {
        return Car(
            id = viewModel.carToSave?.value?.id ?: 0,
            make = binding.makeText.text.toString(),
            model = binding.modelText.text.toString(),
            productionYear = binding.productionYearText.text.toString().toInt(),
            mileage = binding.mileageText.text.toString().toDouble(),
            basePrice = binding.purchasePriceText.text.toString().toDouble(),
            userId = account.id,
            engineId = viewModel.carToSave?.value?.engineId
        )
    }

    private fun setUpConnectionErrorListener() {
        viewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackbar(requireActivity().getString(R.string.internet_connection_error))
                viewModel.connectionErrorHandled()
            }
        })
    }

    private fun setUpTokenExpirationListener() {
        viewModel.tokenExpired.observe(viewLifecycleOwner, Observer {
            if (it) {
                SignInFragment.getAccount(requireContext()) { account ->
                    updateRepositoryAccount(
                        account
                    )
                }
            }
        })
    }

    private fun updateRepositoryAccount(account: GoogleSignInAccount) {
        repository.updateToken(account)
        viewModel.expiredTokenHandled()
        binding.editCarCard.saveButton.performClick()
    }

    private fun setUpOperationSuccessListener() {
        viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                parentFragmentManager.popBackStack()
                viewModel.navigatedOnSuccess()
            }
        })
    }

    private fun setUpOperationFailedListener() {
        viewModel.unknownError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackbar(requireActivity().getString(R.string.operation_failed_error))
                viewModel.unknownErrorHandled()
            }
        })
    }

    private fun setUpHideKeyboardOnBackgroundClickListener() {
        binding.editCarCard.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun showSnackbar(message: String) {
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

enum class Action {
    SAVE, UPDATE
}