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

class CarInformationViewModel(private val carId: Long, private val repository: CarRepository) :
    ViewModel() {

    val car: LiveData<Car> = repository.getCarById(carId)
    val engine: LiveData<Engine> = repository.getEngineByCarId(carId)
    val fuelExpenses: LiveData<List<FuelExpense>> = repository.getFuelExpensesByCarId(carId)

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

    private val _navigateToAddEngine = MutableLiveData<Boolean>(false)
    val navigateToAddEngine: LiveData<Boolean>
        get() = _navigateToAddEngine

    fun doneNavigatingToAddEngine() {
        _navigateToAddEngine.value = false
    }

    private val _navigateToTakePhoto = MutableLiveData<Boolean>(false)
    val navigateToTakePhoto: LiveData<Boolean>
        get() = _navigateToTakePhoto

    fun doneNavigatingToTakePhoto() {
        _navigateToTakePhoto.value = false
    }

    fun deleteFuelExpense(fuelExpenseId: Long) {
        viewModelScope.launch {
            when (repository.deleteFuelExpense(fuelExpenseId)) {
                is ApiResult.AuthenticationError -> _tokenExpired.value = true
                is ApiResult.NetworkError -> _connectionError.value = true
                else -> _unknownError.value = true
            }
        }
    }

    fun addEngineClicked() {
        _navigateToAddEngine.value = true
    }

    fun addPhotoClicked() {
        _navigateToTakePhoto.value = true
    }
}