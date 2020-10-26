package com.misiak.android.autoexpense.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.misiak.android.autoexpense.database.entity.User

@Dao
interface UserDao {
    @Query("select * from user u where u.userId = :id")
    fun getUser(id: String): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)
}