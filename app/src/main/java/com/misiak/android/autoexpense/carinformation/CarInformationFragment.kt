package com.misiak.android.autoexpense.carinformation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentCarInformationBinding
import com.misiak.android.autoexpense.repository.CarRepository

class CarInformationFragment : Fragment() {

    private lateinit var viewModel: CarInformationViewModel
    private lateinit var binding: FragmentCarInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_car_information, container, false)

        val carId = arguments?.let { CarInformationFragmentArgs.fromBundle(it).carId }
        val account = arguments?.let { CarInformationFragmentArgs.fromBundle(it).account }
        val database = getDatabase(requireContext().applicationContext)
        val repository = CarRepository(database, account!!)
        val adapter = FuelExpenseAdapter(FuelExpenseActionListener { fuelExpenseId, action ->
            fuelExpenseActionHandler(fuelExpenseId, action)
        })
        val fuelExpenseItemTouchHelper = FuelExpenseItemTouchHelper()
        val itemTouchHelper = ItemTouchHelper(fuelExpenseItemTouchHelper)
        val viewModelFactory = CarInformationViewModelFactory(carId!!, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CarInformationViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.fuelExpenseRecycler.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.fuelExpenseRecycler)

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
            ItemTouchHelper.RIGHT -> println("Editing ...")
        }
    }

}