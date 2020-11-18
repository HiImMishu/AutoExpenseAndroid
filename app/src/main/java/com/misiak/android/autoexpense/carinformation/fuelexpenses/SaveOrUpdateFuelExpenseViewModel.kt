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

    private var _tokenExpired = MutableLiveData<Boolean>(false)
    val tokenExpired: LiveData<Boolean>
        get() = _tokenExpired

    fun tokenRefreshed() {
        _tokenExpired.value = false
    }

    private var _connectionError = MutableLiveData<Boolean>(false)
    val connectionError: LiveData<Boolean>
        get() = _connectionError

    fun connectionErrorHandled() {
        _connectionError.value = false
    }

    private var _unknownError = MutableLiveData<Boolean>(false)
    val unknownError: LiveData<Boolean>
        get() = _unknownError

    fun unknownErrorHandled() {
        _unknownError.value = false
    }

    fun editFuelExpense(fuelExpenseId: Long) {
        viewModelScope.launch {
            fuelExpenseToSave = repository.getFuelExpenseById(fuelExpenseId)
        }
    }

    fun updateFuelExpense(fuelExpense: FuelExpense) {
        viewModelScope.launch {
            handleResult(repository.updateFuelExpense(fuelExpense))
        }
    }

    fun saveFuelExpense(fuelExpense: FuelExpense) {
        viewModelScope.launch {
            handleResult(repository.saveFuelExpense(fuelExpense))
        }
    }

    private fun handleResult(result: ApiResult) {
        when (result) {
            is ApiResult.Success<*> -> _updateOrSaveCompleted.value = true
            is ApiResult.AuthenticationError -> _tokenExpired.value = true
            is ApiResult.NetworkError -> _connectionError.value = true
            else -> _unknownError.value = true
        }
    }
}