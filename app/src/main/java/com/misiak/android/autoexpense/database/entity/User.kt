package com.misiak.android.autoexpense.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class User(
    @PrimaryKey
    val userId: String,
    val email: String,
    val lastName: String,
    val firstName: String
    //val signedAt: Timestamp
)