package com.misiak.android.autoexpense.mainscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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
import com.misiak.android.autoexpense.databinding.FragmentMainScreenBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.Action
import com.misiak.android.autoexpense.repository.CarRepository

class MainScreenFragment() : FragmentWithOverflowMenu() {

    private lateinit var mainScreenViewModel: MainScreenViewModel
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var repository: CarRepository
    private lateinit var account: GoogleSignInAccount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_screen, container, false)
        account = MainScreenFragmentArgs.fromBundle(requireArguments()).account
        val database = getDatabase(requireContext().applicationContext)
        repository = CarRepository(database, account)
        val mainScreeViewModelFactory = MainScreeViewModelFactory(repository)
        mainScreenViewModel =
            ViewModelProvider(this, mainScreeViewModelFactory).get(MainScreenViewModel::class.java)
        val adapter = CarAdapter(carActionListener(account))
        val carItemTouchHelperCallback = CarItemTouchHelper()

        itemTouchHelper = ItemTouchHelper(carItemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.carList)
        binding.carList.adapter = adapter
        binding.viewModel = mainScreenViewModel
        binding.lifecycleOwner = this

        setUpCarsListDataListener(adapter)
        setUpConnectionErrorListener()
        setUpOperationFailedListener()
        setUpTokenExpirationListener()
        setUpAddCarButtonListener(account)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    private fun setUpCarsListDataListener(adapter: CarAdapter) {
        mainScreenViewModel.carsWithLastFuelExpense.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

    private fun setUpConnectionErrorListener() {
        mainScreenViewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar(requireActivity().getString(R.string.internet_connection_error))
                mainScreenViewModel.connectionErrorHandled()
            }
        })
    }

    private fun setUpOperationFailedListener() {
        mainScreenViewModel.serverError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar(requireActivity().getString(R.string.operation_failed_error))
                mainScreenViewModel.serverErrorHandled()
            }
        })
    }

    private fun setUpTokenExpirationListener() {
        mainScreenViewModel.tokenExpired.observe(viewLifecycleOwner, Observer {
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
        this.account = account
        repository.updateToken(account)
        mainScreenViewModel.tokenExpiredHandled()
    }

    private fun setUpAddCarButtonListener(account: GoogleSignInAccount) {
        binding.addCar.setOnClickListener {
            navigateToEditCar(-1, account, Action.SAVE)
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun carActionListener(account: GoogleSignInAccount): CarClickListener {
        return CarClickListener { carId, actionType ->
            when (actionType) {
                ItemTouchHelper.ACTION_STATE_IDLE -> navigateToCarInfo(carId, account)
                ItemTouchHelper.LEFT -> mainScreenViewModel.deleteCar(carId)
                ItemTouchHelper.RIGHT -> navigateToEditCar(
                    carId,
                    account,
                    Action.UPDATE
                )
            }
        }
    }

    private fun navigateToEditCar(
        carId: Long,
        account: GoogleSignInAccount,
        action: Action
    ) {
        findNavController().navigate(
            MainScreenFragmentDirections.actionMainScreenFragmentToSaveOrUpdateCarFragment(
                account,
                carId,
                action
            )
        )
    }

    private fun navigateToCarInfo(
        carId: Long,
        account: GoogleSignInAccount
    ) {
        findNavController().navigate(
            MainScreenFragmentDirections.actionMainScreenFragmentToCarInformationFragment(
                carId,
                account
            )
        )
    }
}