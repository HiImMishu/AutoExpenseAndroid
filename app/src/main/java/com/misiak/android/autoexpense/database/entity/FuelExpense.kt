package com.misiak.android.autoexpense.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.misiak.android.autoexpense.network.dto.NetworkFuelExpense
import java.util.*

@Entity
data class FuelExpense(
    @PrimaryKey val fuelExpenseId: Long,
    val price: Double,
    val litres: Double,
    val carMileageAfterRefuel: Double,
    val carId: Long,
    val expenseDate: Date,
    val averageCost: Double,
    val averageConsumption: Double
)

fun FuelExpense.asNetworkModel(): NetworkFuelExpense {
    return NetworkFuelExpense(
        id = fuelExpenseId,
        price = price,
        litres = litres,
        mileage = carMileageAfterRefuel,
        averageCost = averageCost,
        averageConsumption = averageConsumption,
        expenseDate = expenseDate,
        car = null
    )
}