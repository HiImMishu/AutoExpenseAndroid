package com.misiak.android.autoexpense.carinformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.misiak.android.autoexpense.FragmentWithOverflowMenu
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.authentication.SignInFragment
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentCarInformationBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.Action
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.android.synthetic.main.fragment_save_or_update_car.view.*
import kotlin.properties.Delegates

class CarInformationFragment : FragmentWithOverflowMenu() {

    private lateinit var viewModel: CarInformationViewModel
    private lateinit var binding: FragmentCarInformationBinding
    private lateinit var account: GoogleSignInAccount
    private lateinit var repository: CarRepository
    private var carId by Delegates.notNull<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_information, container, false)
        carId = CarInformationFragmentArgs.fromBundle(requireArguments()).carId
        account = CarInformationFragmentArgs.fromBundle(requireArguments()).account
        val database = getDatabase(requireContext().applicationContext)
        repository = CarRepository(database, account)
        val adapter = FuelExpenseAdapter(FuelExpenseActionListener { fuelExpenseId, action ->
            fuelExpenseActionHandler(fuelExpenseId, action)
        })
        val fuelExpenseItemTouchHelper = FuelExpenseItemTouchHelper()
        val itemTouchHelper = ItemTouchHelper(fuelExpenseItemTouchHelper)
        val viewModelFactory = CarInformationViewModelFactory(carId, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CarInformationViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.fuelExpenseRecycler.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.fuelExpenseRecycler)

        setUpAddEngineButtonListener()
        setUpCarDataListener()
        setUpFuelExpenseDataListener(adapter)
        setUpAddFuelExpenseButtonListener()
        setUpTokenExpirationListener()
        setUpConnectionErrorListener()
        setUpOperationErrorListener()

        return binding.root
    }

    private fun fuelExpenseActionHandler(fuelExpenseId: Long, actionType: Int) {
        when (actionType) {
            ItemTouchHelper.LEFT -> viewModel.deleteFuelExpense(fuelExpenseId)
            ItemTouchHelper.RIGHT -> navigateToSaveOrUpdateFragment(Action.UPDATE, fuelExpenseId)
        }
    }

    private fun setUpAddEngineButtonListener() {
        viewModel.navigateToAddEngine.observe(viewLifecycleOwner, Observer {
            if (it) {
                navigateToSaveOrUpdateEngineFragment(Action.SAVE, -1)
                viewModel.doneNavigatingToAddEngine()
            }
        })
    }

    private fun navigateToSaveOrUpdateEngineFragment(actionType: Action, engineId: Long) {
        findNavController().navigate(
            CarInformationFragmentDirections.actionCarInformationFragmentToSaveOrUpdateEngineFragment(
                account,
                engineId,
                actionType,
                carId
            )
        )
    }

    private fun setUpCarDataListener() {
        viewModel.car.observe(viewLifecycleOwner, Observer { car ->
            car?.let {
                if (it.engineId == null)
                    binding.expandableEngineInfo.setAdapter(EngineInformationAdapter(null, editEngineAction()))
                else
                    setEngineObserver()
            }
        })
    }

    private fun editEngineAction(): EngineActionListener {
        return EngineActionListener { engineId ->
            navigateToSaveOrUpdateEngineFragment(Action.UPDATE, engineId)
        }
    }

    private fun setEngineObserver() {
        viewModel.engine.observe(viewLifecycleOwner, Observer { engine ->
            engine?.let {
                binding.expandableEngineInfo.setAdapter(EngineInformationAdapter(it, editEngineAction()))
            }
        })
    }

    private fun setUpFuelExpenseDataListener(adapter: FuelExpenseAdapter) {
        viewModel.fuelExpenses.observe(viewLifecycleOwner, Observer { fuelExpenses ->
            fuelExpenses?.let {
                adapter.submitList(it)
            }
        })
    }

    private fun setUpAddFuelExpenseButtonListener() {
        binding.addFuelExpense.setOnClickListener {
            navigateToSaveOrUpdateFragment(
                Action.SAVE,
                -1
            )
        }
    }

    private fun navigateToSaveOrUpdateFragment(actionType: Action, fuelExpenseId: Long) {
        findNavController().navigate(
            CarInformationFragmentDirections.actionCarInformationFragmentToSaveOrUpdateFuelExpenseFragment(
                account,
                fuelExpenseId,
                actionType,
                carId
            )
        )
    }

    private fun setUpTokenExpirationListener() {
        viewModel.tokenExpired.observe(viewLifecycleOwner, Observer { tokenExpired ->
            if (tokenExpired) {
                SignInFragment.getAccount(requireContext()) { account ->
                    updateRepositoryAccount(account)
                }
            }
        })
    }

    private fun updateRepositoryAccount(account: GoogleSignInAccount) {
        repository.updateToken(account)
        showSnackBar(requireActivity().getString(R.string.retry))
        viewModel.tokenRefreshed()
    }

    private fun setUpOperationErrorListener() {
        viewModel.unknownError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar(requireActivity().getString(R.string.operation_failed_error))
                viewModel.unknownErrorHandled()
            }
        })
    }

    private fun setUpConnectionErrorListener() {
        viewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar(requireActivity().getString(R.string.internet_connection_error))
                viewModel.connectionErrorHandled()
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
}