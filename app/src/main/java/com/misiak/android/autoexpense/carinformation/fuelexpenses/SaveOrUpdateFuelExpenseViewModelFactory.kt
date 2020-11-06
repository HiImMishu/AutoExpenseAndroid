package com.misiak.android.autoexpense.carinformation.fuelexpenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misiak.android.autoexpense.repository.FuelExpenseRepository
import java.lang.IllegalArgumentException

class SaveOrUpdateFuelExpenseViewModelFactory(private val repository: FuelExpenseRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveOrUpdateFuelExpenseViewModel::class.java))
            return SaveOrUpdateFuelExpenseViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}