package com.misiak.android.autoexpense.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.network.Network

class CarRepository(private val database: AutoExpenseDatabase,var account: GoogleSignInAccount) {

    suspend fun refreshCars(): ApiResult {
        try {
            val cars = Network.cars.getCars("Bearer ${account.idToken!!}").await()
            val result = ApiResult.apiResultFromCode(cars.code())
            if (result is ApiResult.Success<*>) {
                //TODO("Update database")
            }
            return result
        } catch (e: Exception) {
            return ApiResult.NetworkError(e)
        }
    }

    fun getCars(): LiveData<List<Car>> {
        return database.carDao.getCars(account.id!!)
    }
}