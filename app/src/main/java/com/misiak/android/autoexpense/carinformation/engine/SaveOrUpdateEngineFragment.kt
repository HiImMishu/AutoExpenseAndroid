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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.misiak.android.autoexpense.FragmentWithOverflowMenu
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.authentication.SignInFragment
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentSaveOrUpdateEngineBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.Action
import com.misiak.android.autoexpense.repository.EngineRepository
import kotlinx.android.synthetic.main.fragment_save_or_update_car.view.*

class SaveOrUpdateEngineFragment : FragmentWithOverflowMenu() {

    private lateinit var binding: FragmentSaveOrUpdateEngineBinding
    private lateinit var viewModel: SaveOrUpdateEngineViewModel
    private lateinit var repository: EngineRepository

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
        repository = EngineRepository(database, account)
        val viewModelFactory = SaveOrUpdateEngineViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SaveOrUpdateEngineViewModel::class.java)

        if (action == Action.UPDATE)
            editEngine(engineId)

        setUpSuccessListener()
        setUpTokenExpirationListener()
        setUpConnectionErrorListener()
        setUpOperationFailedListener()
        setUpSaveButtonClickListener(carId, action)
        setUpHideKeyboardOnBackgroundClickListener()

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

    private fun fillEngineValuesToUpdateScree(engine: Engine) {
        binding.capacityText.setText(engine.capacity.toString())
        binding.horsepowerText.setText(engine.horsepower.toString())
        binding.cylindersText.setText(engine.cylinders.toString())
    }

    private fun setUpSuccessListener() {
        viewModel.updateOrSaveCompleted.observe(viewLifecycleOwner, Observer { statusActive ->
            if (statusActive) {
                parentFragmentManager.popBackStack()
                viewModel.updateOrSaveOperationHandled()
            }
        })
    }

    private fun setUpTokenExpirationListener() {
        viewModel.tokenExpired.observe(viewLifecycleOwner, Observer { tokenIsExpired ->
            if (tokenIsExpired)
                SignInFragment.getAccount(requireContext()) { account ->
                    updateRepositoryAccount(account)
                }
        })
    }

    private fun updateRepositoryAccount(account: GoogleSignInAccount) {
        repository.updateToken(account)
        viewModel.tokenRefreshed()
        binding.editEngineCard.saveButton.performClick()
    }

    private fun setUpConnectionErrorListener() {
        viewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar(requireActivity().getString(R.string.internet_connection_error))
                viewModel.connectionErrorHandled()
            }
        })
    }

    private fun setUpOperationFailedListener() {
        viewModel.unknownError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar(requireActivity().getString(R.string.operation_failed_error))
                viewModel.unknownErrorHandled()
            }
        })
    }

    private fun setUpSaveButtonClickListener(
        carId: Long,
        action: Action
    ) {
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

    private fun extractEngine(carId: Long): Engine {
        return Engine(
            id = viewModel.engineToSave?.value?.id ?: 0,
            capacity = binding.capacityText.text.toString().toDouble(),
            horsepower = binding.horsepowerText.text.toString().toDouble(),
            cylinders = binding.cylindersText.text.toString().toInt(),
            carId = carId
        )
    }

    private fun setUpHideKeyboardOnBackgroundClickListener() {
        binding.editEngineCard.setOnClickListener {
            hideKeyboard()
        }
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