package com.misiak.android.autoexpense.repository

import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.database.entity.carAsNetworkCar
import com.misiak.android.autoexpense.database.view.CarWithLastFuelExpenseView
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.network.Network
import com.misiak.android.autoexpense.network.dto.NetworkCar
import com.misiak.android.autoexpense.network.dto.NetworkUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarRepository(private val database: AutoExpenseDatabase, var account: GoogleSignInAccount) {

    private val token = "Bearer ${account.idToken!!}"

    suspend fun refreshCars(): ApiResult {
        return try {
            val serverResponse = Network.cars.getCarsAsync(token).await()
            val result = ApiResult.apiResultFromResponse(serverResponse)
            if (isSuccess(result)) {
                saveToDatabase(serverResponse.body())
            }
            result
        } catch (e: Exception) {
            throw e
            ApiResult.NetworkError(e)
        }
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

    suspend fun saveCar(car: Car): ApiResult {
        val carToSave = car.carAsNetworkCar()
        val userResult = getUserToSavedCar()

        return if (userResult !is ApiResult.Success<*>)
            userResult
        else {
            carToSave.user = userResult.data as NetworkUser
            val serverResponse = saveCarToServer(carToSave)
            if (serverResponse is ApiResult.Success<*>)
                saveCarToDatabase(serverResponse.data as NetworkCar)
            serverResponse
        }
    }

    private suspend fun getUserToSavedCar(): ApiResult {
        return try {
            val userResponse = Network.users.getUserAsync(token).await()
            ApiResult.apiResultFromResponse(userResponse)
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    private suspend fun saveCarToServer(car: NetworkCar): ApiResult {
        return try {
            val carResponse = Network.cars.saveCarAsync(token, car).await()
            var result = ApiResult.apiResultFromResponse(carResponse)
            if (isSuccess(result))
                result = ApiResult.Success<NetworkCar>(carResponse.body()!!)
            result
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    private suspend fun saveCarToDatabase(car: NetworkCar) {
        withContext(Dispatchers.IO) {
            database.carDao.saveCars(*carsAsDatabaseModel(listOf(car)))
        }
    }

    suspend fun updateCar(car: Car): ApiResult {
        val carToUpdateFetchResult = getCarFromServerToUpdate(car.id)

        return if (carToUpdateFetchResult !is ApiResult.Success<*>)
            carToUpdateFetchResult
        else {
            val updatedNetworkCar = updateCarInfo(carToUpdateFetchResult.data as NetworkCar, car)
            val result = saveUpdatedCarToServer(updatedNetworkCar)
            if (isSuccess(result))
                updateCarInDatabase(car)
            result
        }
    }

    private suspend fun getCarFromServerToUpdate(carId: Long): ApiResult {
        return try {
            val serverResponse = Network.cars.getCarAsync(token, carId).await()
            ApiResult.apiResultFromResponse(serverResponse)
        } catch (e: Exception) {
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

    private suspend fun saveUpdatedCarToServer(networkCar: NetworkCar): ApiResult {
        return try {
            val updateCarResponse = Network.cars.updateCarAsync(token, networkCar).await()
            ApiResult.apiResultFromResponse(updateCarResponse)
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    suspend fun deleteCar(carId: Long): ApiResult {
        return try {
            val serverResponse = Network.cars.deleteCarAsync(token, carId).await()
            val result = ApiResult.apiResultFromResponse(serverResponse)
            if (isSuccess(result)) {
                deleteFromDatabase(carId)
            }
            result
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    @Transaction
    private suspend fun deleteFromDatabase(carId: Long) {
        withContext(Dispatchers.IO) {
            database.carDao.deleteCarById(carId)
            database.carDao.deleteFuelExpensesByCarId(carId)
            database.carDao.deleteEngineByCarId(carId)
        }
    }

    private fun isSuccess(result: ApiResult) =
        result is ApiResult.Success<*>

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
                        carMileageAfterRefuel = it.mileage,
                        carId = car.id,
                        expenseDate = it.expenseDate,
                        averageCost = it.averageCost,
                        averageConsumption = it.averageConsumption
                    )
                }!!
            )
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