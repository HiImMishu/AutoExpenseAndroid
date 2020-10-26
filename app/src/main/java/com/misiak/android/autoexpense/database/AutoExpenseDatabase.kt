package com.misiak.android.autoexpense.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.misiak.android.autoexpense.database.dao.CarDao
import com.misiak.android.autoexpense.database.dao.UserDao
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.database.entity.User

@Database(entities = [User::class, Car::class], version = 2, exportSchema = false)
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