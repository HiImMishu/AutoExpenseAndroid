package com.misiak.android.autoexpense.network.service

import com.misiak.android.autoexpense.network.dto.NetworkEngine
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface EngineService {
    @POST("/cars/engines")
    fun saveEngineAsync(@Header("Authorization") token: String, @Body engine: NetworkEngine, @Query("car_id") carId: Long): Deferred<Response<NetworkEngine>>
}