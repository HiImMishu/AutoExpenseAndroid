package com.misiak.android.autoexpense.carinformation.fuelexpenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.repository.FuelExpenseRepository
import kotlinx.coroutines.launch

class SaveOrUpdateFuelExpenseViewModel(val repository: FuelExpenseRepository): ViewModel() {

    var fuelExpenseToSave: LiveData<FuelExpense>? = null

    private var _updateOrSaveCompleted = MutableLiveData<Boolean>(false)
    val updateOrSaveCompleted: LiveData<Boolean>
        get() = _updateOrSaveCompleted

    fun updateOrSaveOperationHandled() {
        _updateOrSaveCompleted.value = false
    }

    fun editFuelExpense(fuelExpenseId: Long) {
        viewModelScope.launch {
            fuelExpenseToSave = repository.getFuelExpenseById(fuelExpenseId)
        }
    }

    fun updateFuelExpense(fuelExpense: FuelExpense) {
        viewModelScope.launch {
            when (val result = repository.updateFuelExpense(fuelExpense)) {
                is ApiResult.Success<*> -> _updateOrSaveCompleted.value = true
                else -> println(result)
            }
        }
    }

}