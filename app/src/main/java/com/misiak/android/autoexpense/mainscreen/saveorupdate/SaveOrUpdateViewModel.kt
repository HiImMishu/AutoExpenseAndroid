package com.misiak.android.autoexpense.mainscreen.saveorupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.coroutines.launch

class SaveOrUpdateViewModel(val repository: CarRepository) : ViewModel() {

    private val _connectionError = MutableLiveData<Boolean>(false)
    val connectionError: LiveData<Boolean>
        get() = _connectionError

    fun connectionErrorHandled() {
        _connectionError.value = false
    }

    private val _updateSuccess = MutableLiveData<Boolean>(false)
    val updateSuccess: LiveData<Boolean>
        get() = _updateSuccess

    fun navigatedOnSuccess() {
        _updateSuccess.value = false
    }

    var carToSave: LiveData<Car>? = null

    fun editCar(carId: Long) {
        viewModelScope.launch {
            carToSave = repository.getCarById(carId)
        }
    }

    fun updateCar(car: Car) {
        viewModelScope.launch {
            when (repository.updateCar(car)) {
                is ApiResult.NetworkError -> _connectionError.value = true
                is ApiResult.Success<*> -> _updateSuccess.value = true
            }
        }
    }
}