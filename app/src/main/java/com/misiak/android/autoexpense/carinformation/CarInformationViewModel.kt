package com.misiak.android.autoexpense.carinformation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.coroutines.launch

class CarInformationViewModel(carId: Long, private val repository: CarRepository) :
    ViewModel() {

    val car: LiveData<Car> = repository.getCarById(carId)
    val engine: LiveData<Engine> = repository.getEngineByCarId(carId)
    val fuelExpenses: LiveData<List<FuelExpense>> = repository.getFuelExpensesByCarId(carId)

    private val _connectionError = MutableLiveData<Boolean>(false)
    val connectionError: LiveData<Boolean>
        get() = _connectionError

    private val _serverError = MutableLiveData<Boolean>(false)
    val serverError: LiveData<Boolean>
        get() = _serverError

    fun deleteFuelExpense(fuelExpenseId: Long) {
        viewModelScope.launch {
            when (repository.deleteFuelExpense(fuelExpenseId)) {
                is ApiResult.NetworkError -> _connectionError.value = true
                is ApiResult.ServerError -> _serverError.value = true
            }
        }
    }
}