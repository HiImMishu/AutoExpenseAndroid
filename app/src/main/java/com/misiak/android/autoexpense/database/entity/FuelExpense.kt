package com.misiak.android.autoexpense.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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