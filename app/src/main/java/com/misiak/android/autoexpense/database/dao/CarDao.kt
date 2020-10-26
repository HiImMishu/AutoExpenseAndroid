package com.misiak.android.autoexpense.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.User

@Dao
interface CarDao {
    @Query("select * from car c where c.userId = :userId")
    fun getCars(userId: String): LiveData<List<Car>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCars(vararg cars: Car)
}