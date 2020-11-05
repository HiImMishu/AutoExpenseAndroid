package com.misiak.android.autoexpense.network.dto

import com.misiak.android.autoexpense.database.entity.Car
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class NetworkUser (
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val signedAt: Date,
    val cars: List<Car>?
)