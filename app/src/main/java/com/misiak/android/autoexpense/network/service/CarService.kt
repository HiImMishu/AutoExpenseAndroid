package com.misiak.android.autoexpense.network.service

import com.misiak.android.autoexpense.network.dto.NetworkCar
import com.misiak.android.autoexpense.network.dto.NetworkCarContainer
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface CarService {
    @GET("/cars")
    fun getCars(@Header("Authorization") token: String): Deferred<Response<List<NetworkCar>>>
}