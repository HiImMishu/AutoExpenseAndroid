package com.misiak.android.autoexpense.network.service

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path

interface FuelExpenseService {

    @DELETE("/cars/fuel-expenses/{id}")
    fun deleteFuelExpenseAsync(@Header("Authorization") token: String, @Path("id") fuelExpenseId: Long): Deferred<Response<Unit>>
}