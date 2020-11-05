package com.misiak.android.autoexpense.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.misiak.android.autoexpense.network.dto.NetworkEngine

@Entity
data class Engine(
    @PrimaryKey val id: Long,
    val capacity: Double,
    val horsepower: Double,
    val cylinders: Int,
    val carId: Long
)

fun Engine.asNetworkEngine(): NetworkEngine {
    return NetworkEngine(
        id = id,
        capacity = capacity,
        horsepower = horsepower,
        cylinders = cylinders
    )
}