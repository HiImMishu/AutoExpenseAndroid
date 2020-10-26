package com.misiak.android.autoexpense.network.dto

import com.misiak.android.autoexpense.network.dto.NetworkUser
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkCar (
    val id: Long,
    val make: String,
    val model: String,
    val productionYear: Int?,
    val mileage: Double?,
    val basePrice: Double?
    // val engine: NetworkEngine,
    //val fuelExpenses: List<NetworkFuelExpenses>
)