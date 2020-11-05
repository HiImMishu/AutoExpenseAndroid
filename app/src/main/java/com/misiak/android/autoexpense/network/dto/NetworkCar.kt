package com.misiak.android.autoexpense.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkCar(
    val id: Long,
    var make: String,
    var model: String,
    var productionYear: Int,
    var mileage: Double,
    var basePrice: Double?,
    val engine: NetworkEngine?,
    var user: NetworkUser?,
    val fuelExpenses: List<NetworkFuelExpense>?
){
}

