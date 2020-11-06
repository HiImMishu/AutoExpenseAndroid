package com.misiak.android.autoexpense.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.FuelExpense

class FuelExpenseRepository(private val database: AutoExpenseDatabase, val account: GoogleSignInAccount) {

    suspend fun getFuelExpenseById(fuelExpenseId: Long): LiveData<FuelExpense> {
        return database.carDao.getFuelExpenseById(fuelExpenseId)
    }
}