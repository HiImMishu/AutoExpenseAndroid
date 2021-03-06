package com.misiak.android.autoexpense.mainscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misiak.android.autoexpense.database.view.CarWithLastFuelExpenseView
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.coroutines.launch

class MainScreenViewModel(private val repository: CarRepository) : ViewModel() {

    val carsWithLastFuelExpense: LiveData<List<CarWithLastFuelExpenseView>> =
        repository.getCarsWithRecentFuelExpense()

    private val _resultIsEmpty = MutableLiveData<Boolean>(false)
    val resultIsEmpty: LiveData<Boolean>
        get() = _resultIsEmpty

    private val _connectionError = MutableLiveData<Boolean>(false)
    val connectionError: LiveData<Boolean>
        get() = _connectionError

    private val _serverError = MutableLiveData<Boolean>(false)
    val serverError: LiveData<Boolean>
        get() = _serverError

    private val _tokenExpired = MutableLiveData<Boolean>(false)
    val tokenExpired: LiveData<Boolean>
        get() = _tokenExpired

    init {
        refreshData()
        carsWithLastFuelExpense.observeForever { list ->
            _resultIsEmpty.value = list.isEmpty()
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            when (repository.refreshCars()) {
                is ApiResult.NetworkError -> _connectionError.value = true
                is ApiResult.ServerError -> _serverError.value = true
                is ApiResult.AuthenticationError -> _tokenExpired.value = true
            }
        }
    }

    fun connectionErrorHandled() {
        _connectionError.value = false
    }

    fun serverErrorHandled() {
        _serverError.value = false
    }

    fun tokenExpiredHandled() {
        _tokenExpired.value = false
        refreshData()
    }

    fun deleteCar(carId: Long) {
        viewModelScope.launch {
            when (repository.deleteCar(carId)) {
                is ApiResult.NetworkError -> _connectionError.value = true
                is ApiResult.ServerError -> _serverError.value = true
                is ApiResult.AuthenticationError -> _tokenExpired.value = true
            }
        }
    }
}