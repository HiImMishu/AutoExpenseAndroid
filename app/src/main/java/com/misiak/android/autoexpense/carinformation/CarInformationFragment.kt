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
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentCarInformationBinding
import com.misiak.android.autoexpense.mainscreen.saveorupdate.Action
import com.misiak.android.autoexpense.repository.CarRepository
import kotlin.properties.Delegates

class CarInformationFragment : Fragment() {

    private lateinit var viewModel: CarInformationViewModel
    private lateinit var binding: FragmentCarInformationBinding
    private lateinit var account: GoogleSignInAccount
    private var carId by Delegates.notNull<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_information, container, false)
        carId = CarInformationFragmentArgs.fromBundle(requireArguments()).carId
        account = CarInformationFragmentArgs.fromBundle(requireArguments()).account
        val database = getDatabase(requireContext().applicationContext)
        val repository = CarRepository(database, account)
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

        viewModel.navigateToAddEngine.observe(viewLifecycleOwner, Observer {
            if (it) {
                navigateToSaveOrUpdateEngineFragment(Action.SAVE, -1)
                viewModel.doneNavigatingToAddEngine()
            }
        })

        viewModel.car.observe(viewLifecycleOwner, Observer { car ->
            car?.let {
                if (it.engineId == null)
                    binding.expandableEngineInfo.setAdapter(EngineInformationAdapter(null))
                else
                    setEngineObserver()
            }
        })

        viewModel.fuelExpenses.observe(viewLifecycleOwner, Observer { fuelExpenses ->
            fuelExpenses?.let {
                adapter.submitList(it)
            }
        })

        binding.addFuelExpense.setOnClickListener {
            navigateToSaveOrUpdateFragment(
                Action.SAVE,
                -1
            )
        }

        return binding.root
    }

    private fun setEngineObserver() {
        viewModel.engine.observe(viewLifecycleOwner, Observer { engine ->
            engine?.let {
                binding.expandableEngineInfo.setAdapter(EngineInformationAdapter(it))
            }
        })
    }

    private fun fuelExpenseActionHandler(fuelExpenseId: Long, actionType: Int) {
        when (actionType) {
            ItemTouchHelper.LEFT -> viewModel.deleteFuelExpense(fuelExpenseId)
            ItemTouchHelper.RIGHT -> navigateToSaveOrUpdateFragment(Action.UPDATE, fuelExpenseId)
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

}