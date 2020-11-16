package com.misiak.android.autoexpense.network.dto

import com.misiak.android.autoexpense.database.entity.FuelExpense
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

fun NetworkFuelExpense.asDatabaseModel(carId: Long = -1): FuelExpense {
    return FuelExpense(
        fuelExpenseId = id,
        price = price,
        litres = litres,
        carMileageAfterRefuel = mileage,
        carId = car?.id ?: carId,
        expenseDate = expenseDate,
        averageCost = averageCost,
        averageConsumption = averageConsumption
    )
}