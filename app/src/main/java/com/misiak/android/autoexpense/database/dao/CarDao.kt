package com.misiak.android.autoexpense.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
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

    @Query("select * from Engine e where e.carId = :carId")
    fun getEngineByCarIdAsync(carId: Long): LiveData<Engine>

    @Query("select * from Engine e where e.carId = :carId")
    fun getEngineByCarId(carId: Long): Engine?

    @Query("update Car set engineId = :engineId where id = :carId")
    fun updateCarEngineId(carId: Long, engineId: Long)

    @Query("select * from Engine e where e.id = :engineId")
    fun getEngineByIdAsync(engineId: Long): LiveData<Engine>

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

    @Query("delete from FuelExpense where FuelExpense.fuelExpenseId = :fuelExpenseId")
    fun deleteFuelExpenseById(fuelExpenseId: Long)

    @Query("select * from FuelExpense fe where fe.fuelExpenseId = :fuelExpenseId")
    fun getFuelExpenseById(fuelExpenseId: Long): LiveData<FuelExpense>
}