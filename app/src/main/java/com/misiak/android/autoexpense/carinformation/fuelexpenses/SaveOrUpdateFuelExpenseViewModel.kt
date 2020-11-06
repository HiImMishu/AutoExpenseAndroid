package com.misiak.android.autoexpense.carinformation.fuelexpenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.repository.FuelExpenseRepository
import kotlinx.coroutines.launch

class SaveOrUpdateFuelExpenseViewModel(val repository: FuelExpenseRepository): ViewModel() {

    var fuelExpenseToSave: LiveData<FuelExpense>? = null

    fun editFuelExpense(fuelExpenseId: Long) {
        viewModelScope.launch {
            fuelExpenseToSave = repository.getFuelExpenseById(fuelExpenseId)
        }
    }
}