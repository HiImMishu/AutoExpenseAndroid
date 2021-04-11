package com.misiak.android.autoexpense.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.misiak.android.autoexpense.network.dto.NetworkCar

@Entity
data class Car(
    @PrimaryKey val id: Long,
    val make: String,
    val model: String,
    val productionYear: Int,
    val mileage: Double,
    val basePrice: Double?,
    var userId: String?,
    var engineId: Long?,
    var photoUrl: String?
)

fun Car.asNetworkCar(): NetworkCar {
    return NetworkCar(
        id = id,
        make = make,
        model = model,
        productionYear = productionYear,
        mileage = mileage,
        basePrice = basePrice,
        photoUrl = photoUrl,
        engine = null,
        user = null,
        fuelExpenses = null
    )
}