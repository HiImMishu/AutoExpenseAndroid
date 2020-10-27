package com.misiak.android.autoexpense.network.service

import com.misiak.android.autoexpense.network.dto.NetworkFuelExpense
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FuelExpenseService {
    @GET("/cars/fuel-expenses")
    fun getCars(@Header("Authorization") token: String, @Query("car_id") carId: Double): Deferred<Response<List<NetworkFuelExpense>>>
}