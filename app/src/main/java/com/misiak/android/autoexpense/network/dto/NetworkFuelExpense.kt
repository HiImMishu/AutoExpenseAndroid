package com.misiak.android.autoexpense.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class NetworkFuelExpense(
    val id: Long,
    val price: Double,
    val litres: Double,
    @Json(name = "milage") val mileage: Double,
    val averageCost: Double,
    val averageConsumption: Double,
    val car: NetworkCar?,
    val expenseDate: Date
    )