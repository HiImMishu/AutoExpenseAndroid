package com.misiak.android.autoexpense.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Car(
    @PrimaryKey val id: Long,
    val make: String,
    val model: String,
    val productionYear: Int?,
    val mileage: Double?,
    val basePrice: Double?,
    var userId: String?,
    var engineId: Long?
)