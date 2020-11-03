package com.misiak.android.autoexpense.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.database.view.CarWithLastFuelExpenseView

@Dao
interface CarDao {
    @Query("select * from car c where c.userId = :userId")
    fun getCars(userId: String): LiveData<List<Car>>

    @Query("select * from CarWithLastFuelExpenseView cf where cf.userId = :userId")
    fun getCarsWithRecentFuelExpense(userId: String): LiveData<List<CarWithLastFuelExpenseView>>

    @Query("select * from Car c where c.id = :carId")
    fun getCarById(carId: Long): LiveData<Car>

    @Query("select * from FuelExpense fe where fe.carId = :carId")
    fun getFuelExpensesByCarIdAsync(carId: Long): LiveData<List<FuelExpense>>

    @Query("select * from FuelExpense fe where fe.carId = :carId")
    fun getFuelExpensesByCarId(carId: Long): List<FuelExpense>

    @Query("select * from Engine e where e.carId = :carId")
    fun getEngineByCarIdAsync(carId: Long): LiveData<Engine>

    @Query("select * from Engine e where e.carId = :carId")
    fun getEngineByCarId(carId: Long): Engine

    @Query("delete from Car where Car.id = :carId")
    fun deleteCarById(carId: Long)

    @Query("delete from FuelExpense where FuelExpense.carId = :carId")
    fun deleteFuelExpensesByCarId(carId: Long)

    @Query("delete from Engine where Engine.carId = :carId")
    fun deleteEngineByCarId(carId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCars(vararg cars: Car)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFuelExpenses(vararg fuelExpenses: FuelExpense)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEngines(vararg engines: Engine)
}