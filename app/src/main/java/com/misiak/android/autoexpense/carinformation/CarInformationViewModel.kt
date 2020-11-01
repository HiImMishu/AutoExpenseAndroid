package com.misiak.android.autoexpense.carinformation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.repository.CarRepository

class CarInformationViewModel(carId: Long, private val repository: CarRepository) :
    ViewModel() {

    val car: LiveData<Car> = repository.getCarById(carId)
    val engine: LiveData<Engine> = repository.getEngineByCarId(carId)
    val fuelExpenses: LiveData<List<FuelExpense>> = repository.getFuelExpensesByCarId(carId)

}