package com.misiak.android.autoexpense.network.service

import com.misiak.android.autoexpense.network.dto.NetworkUser
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {
    @GET("/users")
    fun getUserAsync(@Header("Authorization") token: String): Deferred<Response<NetworkUser>>

    @POST("/users/sign-in")
    fun signInUserAsync(@Header("Authorization") token: String): Deferred<Response<NetworkUser>>
}