package com.misiak.android.autoexpense.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Transaction
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
            val carsResponse = Network.cars.getCarsAsync("Bearer ${account.idToken!!}").await()
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

    suspend fun deleteCar(carId: Long) : ApiResult {
        return try {
            val deleteResponse = Network.cars.deleteCarAsync("Bearer ${account.idToken!!}", carId).await()
            val result = ApiResult.apiResultFromCode(deleteResponse.code())
            if (result is ApiResult.Success<*>) {
                deleteFromDatabase(carId)
            }
            result
        } catch (e: Exception) {
            Log.e(CarRepository::refreshCars.toString(), "Error occurred: ${e.message}")
            ApiResult.NetworkError(e)
        }
    }

    suspend fun updateCar(car: Car) : ApiResult {
        return try {
            val getCarResponse = Network.cars.getCarAsync("Bearer ${account.idToken!!}", car.id).await()
            var result = ApiResult.apiResultFromCode(getCarResponse.code())
            if (result is ApiResult.Success<*>) {
                val updatedNetworkCar = updateCarInfo(getCarResponse.body()!!, car)
                val response: ApiResult = saveUpdatedCarToServer(updatedNetworkCar)
                if (response is ApiResult.Success<*>)
                    updateCarInDatabase(car)
                result = response
            }
            result
        } catch (e: Exception) {
            Log.e(CarRepository::refreshCars.toString(), "Error occurred: ${e.message}")
            ApiResult.NetworkError(e)
        }
    }

    private suspend fun saveUpdatedCarToServer(networkCar: NetworkCar): ApiResult {
        return try {
            val updateCarResponse = Network.cars.updateCarAsync("Bearer ${account.idToken!!}", networkCar).await()
            ApiResult.apiResultFromCode(updateCarResponse.code())
        } catch (e: Exception) {
            Log.e(CarRepository::refreshCars.toString(), "Error occurred: ${e.message}")
            ApiResult.NetworkError(e)
        }
    }

    private fun updateCarInfo(networkCar: NetworkCar, car: Car): NetworkCar {
        networkCar.make = car.make
        networkCar.model = car.model
        networkCar.productionYear = car.productionYear
        networkCar.mileage = car.mileage
        networkCar.basePrice = car.basePrice

        return networkCar
    }

    private suspend fun updateCarInDatabase(car: Car) {
        withContext(Dispatchers.IO) {
            database.carDao.saveCars(car)
        }
    }

    @Transaction
    private suspend fun deleteFromDatabase(carId: Long) {
        withContext(Dispatchers.IO) {
            database.carDao.deleteCarById(carId)
            database.carDao.deleteFuelExpensesByCarId(carId)
            database.carDao.deleteEngineByCarId(carId)
            println("Deleted!")
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

    fun getFuelExpensesByCarId(carId: Long): LiveData<List<FuelExpense>> {
        return database.carDao.getFuelExpensesByCarIdAsync(carId)
    }

    fun getEngineByCarId(carId: Long): LiveData<Engine> {
        return database.carDao.getEngineByCarIdAsync(carId)
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