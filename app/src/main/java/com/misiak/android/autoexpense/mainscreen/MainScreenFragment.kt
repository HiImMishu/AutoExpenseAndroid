package com.misiak.android.autoexpense.mainscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.snackbar.Snackbar
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.authentication.SignInFragment
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentMainScreenBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.Action
import com.misiak.android.autoexpense.repository.CarRepository

class MainScreenFragment() : Fragment() {

    private lateinit var mainScreenViewModel: MainScreenViewModel
    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var repository: CarRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_screen, container, false)
        val account = arguments?.let { MainScreenFragmentArgs.fromBundle(it).account }
        val database = getDatabase(requireContext().applicationContext)
        repository = CarRepository(database, account!!)
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

        mainScreenViewModel.carsWithLastFuelExpense.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        mainScreenViewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar("Check your internet connection.")
                mainScreenViewModel.connectionErrorHandled()
            }
        })

        mainScreenViewModel.serverError.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSnackBar("Server Error, couldn't refresh data.")
                mainScreenViewModel.serverErrorHandled()
            }
        })

        mainScreenViewModel.tokenExpired.observe(viewLifecycleOwner, Observer {
            if (it) {
                SignInFragment.getAccount(requireContext()) { account ->
                    updateRepositoryAccount(
                        account
                    )
                }
            }
        })

        binding.addCar.setOnClickListener {
            navigateToEditCar(-1, account, Action.SAVE)
        }

        (activity as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }

    private fun updateRepositoryAccount(account: GoogleSignInAccount) {
        repository.account = account
        mainScreenViewModel.tokenExpiredHandled()
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