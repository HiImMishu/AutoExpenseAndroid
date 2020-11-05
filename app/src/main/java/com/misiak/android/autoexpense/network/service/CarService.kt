package com.misiak.android.autoexpense.network.service

import com.misiak.android.autoexpense.network.dto.NetworkCar
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface CarService {
    @GET("/cars")
    fun getCarsAsync(@Header("Authorization") token: String): Deferred<Response<List<NetworkCar>>>

    @GET("/cars/{id}")
    fun getCarAsync(@Header("Authorization") token: String, @Path("id") carId: Long): Deferred<Response<NetworkCar>>

    @POST("/cars")
    fun saveCarAsync(@Header("Authorization") token: String, @Body car: NetworkCar): Deferred<Response<NetworkCar>>

    @PUT("/cars")
    fun updateCarAsync(@Header("Authorization") token: String, @Body car: NetworkCar): Deferred<Response<NetworkCar>>

    @DELETE("/cars/{id}")
    fun deleteCarAsync(@Header("Authorization") token: String, @Path("id") carId: Long): Deferred<Response<Unit>>
}