package com.misiak.android.autoexpense.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.database.entity.User
import com.misiak.android.autoexpense.database.view.CarWithLastFuelExpenseView

@Dao
interface CarDao {
    @Query("select * from car c where c.userId = :userId")
    fun getCars(userId: String): LiveData<List<Car>>

    @Query("select * from CarWithLastFuelExpenseView cf where 1 =:userId OR 1=1")
    fun getCarsWithRecentFuelExpense(userId: String): LiveData<List<CarWithLastFuelExpenseView>>

    @Query("select * from FuelExpense")
    fun getFuelExpenses(): LiveData<List<FuelExpense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCars(vararg cars: Car)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFuelExpenses(vararg fuelExpenses: FuelExpense)
}