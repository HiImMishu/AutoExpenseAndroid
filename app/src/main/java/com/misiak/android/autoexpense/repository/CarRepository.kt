package com.misiak.android.autoexpense.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.Car
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

    private suspend fun saveToDatabase(cars: List<NetworkCar>?) {
        withContext(Dispatchers.IO) {
            cars?.let { database.carDao.saveCars(*carsAsDatabaseModel(cars)) }
        }
    }

    fun getCars(): LiveData<List<Car>> {
        return database.carDao.getCars(account.id!!)
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
}