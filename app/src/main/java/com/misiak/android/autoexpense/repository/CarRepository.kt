package com.misiak.android.autoexpense.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.database.view.CarWithLastFuelExpenseView
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.network.Network
import com.misiak.android.autoexpense.network.dto.NetworkCar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarRepository(private val database: AutoExpenseDatabase,var account: GoogleSignInAccount) {

    suspend fun refreshCars(): ApiResult {
        return try {
            val carsResponse = Network.cars.getCars("Bearer ${account.idToken!!}").await()
            val result = ApiResult.apiResultFromCode(carsResponse.code())
            if (result is ApiResult.Success<*>) {
                saveToDatabase(carsResponse.body())
            }
            result
        } catch (e: Exception) {
            Log.e(CarRepository::refreshCars.toString(), "Error occurred: ${e.message}")
            ApiResult.NetworkError(e)
        }
    }

    fun getCars(): LiveData<List<Car>> {
        return database.carDao.getCars(account.id!!)
    }

    fun getCarsWithRecentFuelExpense(): LiveData<List<CarWithLastFuelExpenseView>> {
        return database.carDao.getCarsWithRecentFuelExpense(account.id!!)
    }

    fun getCarById(carId: Long): LiveData<Car> {
        return database.carDao.getCarById(carId)
    }

    fun getFuelExpenses(): LiveData<List<FuelExpense>> {
        return database.carDao.getFuelExpenses()
    }

    fun getEngineByCarId(carId: Long): LiveData<Engine> {
        return database.carDao.getEngineByCarId(carId)
    }

    private suspend fun saveToDatabase(cars: List<NetworkCar>?) {
        withContext(Dispatchers.IO) {
            cars?.let {
                database.carDao.saveCars(*carsAsDatabaseModel(cars))
                database.carDao.saveFuelExpenses(*fuelExpensesAsDatabaseModel(cars))
                database.carDao.saveEngines(*enginesAsDatabaseModel(cars))
            }
        }
    }

    private fun carsAsDatabaseModel(cars: List<NetworkCar>): Array<Car> {
        return cars.map {
            Car(
                id = it.id,
                make = it.make,
                model = it.model,
                productionYear = it.productionYear,
                mileage = it.mileage,
                basePrice = it.basePrice,
                userId = account.id,
                engineId = it.engine?.id
            )
        }.toTypedArray()
    }

    private fun fuelExpensesAsDatabaseModel(cars: List<NetworkCar>): Array<FuelExpense> {
        val fuelExpenseList: MutableList<FuelExpense> = mutableListOf()
        for (car in cars) {
            fuelExpenseList.addAll(
            car.fuelExpenses?.map {
                FuelExpense(
                    fuelExpenseId = it.id,
                    price = it.price,
                    litres = it.litres,
                    milage = it.milage,
                    carId = car.id,
                    expenseDate = it.expenseDate,
                    averageCost = it.averageCost,
                    averageConsumption = it.averageConsumption
                )
            }!!)
        }
        return fuelExpenseList.toTypedArray()
    }

    private fun enginesAsDatabaseModel(cars: List<NetworkCar>): Array<Engine> {
        val engines: MutableList<Engine> = mutableListOf()
        cars.map { networkCar ->
            networkCar.engine?.let {
                engines.add(
                    Engine(
                        id = it.id,
                        capacity = it.capacity,
                        horsepower = it.horsepower,
                        cylinders = it.cylinders,
                        carId = networkCar.id
                    )
                )
            }
        }
        return engines.toTypedArray()
    }
}