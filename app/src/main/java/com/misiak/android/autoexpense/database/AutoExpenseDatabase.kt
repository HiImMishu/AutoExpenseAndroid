package com.misiak.android.autoexpense.database

import android.content.Context
import androidx.room.*
import com.misiak.android.autoexpense.database.dao.CarDao
import com.misiak.android.autoexpense.database.dao.UserDao
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.database.entity.User
import com.misiak.android.autoexpense.database.view.CarWithLastFuelExpenseView

@Database(
    entities = [User::class, Car::class, FuelExpense::class, Engine::class],
    views = [CarWithLastFuelExpenseView::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AutoExpenseDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val carDao: CarDao
}

private lateinit var INSTANCE: AutoExpenseDatabase

fun getDatabase(context: Context): AutoExpenseDatabase {
    synchronized(AutoExpenseDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AutoExpenseDatabase::class.java,
                "autoExpense"
            ).fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}