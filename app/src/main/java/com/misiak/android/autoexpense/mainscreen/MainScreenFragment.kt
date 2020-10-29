package com.misiak.android.autoexpense.mainscreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.misiak.android.autoexpense.R
import com.misiak.android.autoexpense.authentication.SignInFragment
import com.misiak.android.autoexpense.database.getDatabase
import com.misiak.android.autoexpense.databinding.FragmentMainScreenBinding
import com.misiak.android.autoexpense.repository.CarRepository


class MainScreenFragment() : Fragment() {

    private lateinit var mainScreenViewModel: MainScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMainScreenBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main_screen, container, false)
        val account = arguments?.let { MainScreenFragmentArgs.fromBundle(it).account }
        val database = getDatabase(requireContext().applicationContext)
        val repository = CarRepository(database, account!!)
        val mainScreeViewModelFactory = MainScreeViewModelFactory(repository)
        mainScreenViewModel =
            ViewModelProvider(this, mainScreeViewModelFactory).get(MainScreenViewModel::class.java)
        val adapter = CarAdapter(CarClickListener { carId -> mainScreenViewModel.onCarClicked(carId) })

        binding.carList.adapter = adapter
        binding.viewModel = mainScreenViewModel
        binding.lifecycleOwner = this

        mainScreenViewModel.carsWithLastFuelExpence.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        mainScreenViewModel.connectionError.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(requireView(), "Check your internet connection.", Snackbar.LENGTH_LONG).show()
                mainScreenViewModel.connectionErrorHandled()
            }
        })

        mainScreenViewModel.serverError.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(requireView(), "Server Error, couldn't refresh data.", Snackbar.LENGTH_LONG).show()
                mainScreenViewModel.serverErrorHandled()
            }
        })

        mainScreenViewModel.tokenExpired.observe(viewLifecycleOwner, Observer {
            if (it) {
                repository.account = getAccount()!!
                mainScreenViewModel.tokenExpiredHandled()
            }
        })

        (activity as AppCompatActivity).supportActionBar?.show()
        return binding.root
    }



    private fun getAccount(): GoogleSignInAccount? {
        val googleSignInClient = SignInFragment.getGoogleSignInClient(requireContext())
        val task = googleSignInClient.silentSignIn()
        if (task.isComplete) {
            return task.getResult(ApiException::class.java)!!
        }
        else {
            task.addOnCompleteListener {
                return@addOnCompleteListener
            }
            return task.getResult(ApiException::class.java)!!
        }
    }
}