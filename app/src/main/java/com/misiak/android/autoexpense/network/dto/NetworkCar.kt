package com.misiak.android.autoexpense.network.dto

import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.network.dto.NetworkUser
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkCarContainer(val networkCars: List<NetworkCar>)

@JsonClass(generateAdapter = true)
data class NetworkCar(
    val id: Long,
    val make: String,
    val model: String,
    val productionYear: Int?,
    val mileage: Double?,
    val basePrice: Double?,
    val engine: NetworkEngine?,
    val user: NetworkUser?,
    val fuelExpenses: List<NetworkFuelExpense>?
)