package com.misiak.android.autoexpense.mainscreen.saveorupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.coroutines.launch

class SaveOrUpdateViewModel(val repository: CarRepository) : ViewModel() {

    var carToSave: LiveData<Car>? = null

    fun editCar(carId: Long) {
        viewModelScope.launch {
            carToSave = repository.getCarById(carId)
        }
    }

    fun editCarSaved() {
        carToSave = null
    }
}