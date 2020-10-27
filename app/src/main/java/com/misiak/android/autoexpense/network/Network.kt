package com.misiak.android.autoexpense.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.misiak.android.autoexpense.network.service.CarService
import com.misiak.android.autoexpense.network.service.FuelExpenseService
import com.misiak.android.autoexpense.network.service.UserService
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .build()

object Network {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient)
        .build()

    val users = retrofit.create(UserService::class.java)
    val cars = retrofit.create(CarService::class.java)
    val fuelExpenses = retrofit.create(FuelExpenseService::class.java)
}