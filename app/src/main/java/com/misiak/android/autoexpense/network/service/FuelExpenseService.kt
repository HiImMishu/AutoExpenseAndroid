package com.misiak.android.autoexpense.network.service

import com.misiak.android.autoexpense.network.dto.NetworkFuelExpense
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface FuelExpenseService {
    @POST("cars/fuel-expenses")
    fun saveFuelExpenseAsync(@Header("Authorization") token: String, @Body networkFuelExpense: NetworkFuelExpense, @Query("car_id") carId: Long): Deferred<Response<NetworkFuelExpense>>

    @DELETE("/cars/fuel-expenses/{id}")
    fun deleteFuelExpenseAsync(@Header("Authorization") token: String, @Path("id") fuelExpenseId: Long): Deferred<Response<Unit>>

    @PUT("/cars/fuel-expenses")
    fun updateFuelExpenseAsync(@Header("Authorization") token: String, @Body networkFuelExpense: NetworkFuelExpense, @Query("car_id") carId: Long): Deferred<Response<NetworkFuelExpense>>
}