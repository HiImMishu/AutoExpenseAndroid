package com.misiak.android.autoexpense.mainscreen.saveorupdate

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentSaveOrUpdateCarBinding
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.android.synthetic.main.fragment_save_or_update_car.view.*

class SaveOrUpdateCarFragment : Fragment() {

    private lateinit var binding: FragmentSaveOrUpdateCarBinding
    private lateinit var viewModel: SaveOrUpdateViewModel
    private lateinit var account: GoogleSignInAccount

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
        val carId = arguments?.let { SaveOrUpdateCarFragmentArgs.fromBundle(it).carId }
        val action = arguments?.let { SaveOrUpdateCarFragmentArgs.fromBundle(it).action }
        val database = getDatabase(requireContext())
        val repository = CarRepository(database, account!!)
        val viewModelFactory = SaveOrUpdateViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SaveOrUpdateViewModel::class.java)

        if (action == Action.UPDATE)
            editCar(carId!!)

        binding.editCarCard.saveButton.setOnClickListener {
            if (isDataValid()) {
                hideKeyboard()
                val car = extractCar()
                if (action == Action.UPDATE)
                    viewModel.updateCar(car)
            }
        }

        viewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(
                    requireView(),
                    "Check your internet connection.",
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.connectionErrorHandled()
            }
        })

        viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                parentFragmentManager.popBackStack()
            }
        })

        binding.editCarCard.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
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

    private fun isDataValid(): Boolean {
        return true
        //TODO("Validate fields")
    }

    private fun fillCarValuesToEditScreen(car: Car) {
        binding.makeText.setText(car.make)
        binding.modelText.setText(car.model)
        binding.productionYearText.setText(car.productionYear.toString())
        binding.mileageText.setText(car.mileage.toString())
        binding.purchasePriceText.setText(car.basePrice.toString())
    }

    private fun editCar(carId: Long) {
        viewModel.editCar(carId)

        viewModel.carToSave?.observe(viewLifecycleOwner, Observer { car ->
            car?.let {
                fillCarValuesToEditScreen(it)
            }
        })
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