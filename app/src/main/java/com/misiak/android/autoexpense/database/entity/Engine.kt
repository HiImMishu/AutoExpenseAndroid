package com.misiak.android.autoexpense.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Engine(
    @PrimaryKey val id: Long,
    val capacity: Double,
    val horsepower: Double,
    val cylinders: Int,
    val carId: Long
)