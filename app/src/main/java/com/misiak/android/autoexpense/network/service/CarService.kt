package com.misiak.android.autoexpense.network.service

import com.misiak.android.autoexpense.network.dto.NetworkCar
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CarService {
    @GET("/cars")
    fun getCars(@Header("Authorization") token: String): Deferred<Response<List<NetworkCar>>>

    @DELETE("/cars/{id}")
    fun deleteCar(@Header("Authorization") token: String, @Path("id") carId: Long): Deferred<Response<Unit>>
}