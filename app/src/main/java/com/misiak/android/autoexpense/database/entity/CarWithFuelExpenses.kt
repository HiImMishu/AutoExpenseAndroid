package com.misiak.android.autoexpense.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CarWithFuelExpenses(
    @Embedded val car: Car,
    @Relation(
        parentColumn = "id",
        entityColumn = "carId"
    )
    val fuelExpenses: List<FuelExpense>
)