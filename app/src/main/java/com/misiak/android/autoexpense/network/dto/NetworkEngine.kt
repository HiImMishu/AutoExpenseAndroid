package com.misiak.android.autoexpense.network.dto

import com.misiak.android.autoexpense.database.entity.Engine
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkEngine(
    val id: Long,
    val capacity: Double,
    val horsepower: Double,
    val cylinders: Int
)

fun NetworkEngine.asDatabaseEngine(carID: Long): Engine {
    return Engine(
        id = id,
        capacity = capacity,
        horsepower = horsepower,
        cylinders = cylinders,
        carId = carID
    )
}