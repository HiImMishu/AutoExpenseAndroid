package com.misiak.android.autoexpense.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkEngine(
    val id: Long,
    val capacity: Double,
    val horsepower: Double,
    val cylinders: Int
)