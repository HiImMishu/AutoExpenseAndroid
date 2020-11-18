package com.misiak.android.autoexpense.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.FuelExpense
import com.misiak.android.autoexpense.database.entity.asNetworkModel
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.network.Network
import com.misiak.android.autoexpense.network.dto.NetworkFuelExpense
import com.misiak.android.autoexpense.network.dto.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FuelExpenseRepository(private val database: AutoExpenseDatabase, var account: GoogleSignInAccount) {

    private val token = "Bearer ${account.idToken!!}"

    fun getFuelExpenseById(fuelExpenseId: Long): LiveData<FuelExpense> {
        return database.carDao.getFuelExpenseById(fuelExpenseId)
    }

    suspend fun updateFuelExpense(fuelExpense: FuelExpense): ApiResult {
        val fuelExpenseToUpdate = fuelExpense.asNetworkModel()
        val result = updateFuelExpenseOnServer(fuelExpenseToUpdate, fuelExpense.carId)

        if (result is ApiResult.Success<*>)
            saveFuelExpenseToDatabase((result.data as NetworkFuelExpense).asDatabaseModel(fuelExpense.carId))

        return result
    }

    private suspend fun updateFuelExpenseOnServer(fuelExpenseToUpdate: NetworkFuelExpense, carId: Long): ApiResult {
        return try {
            val serverResponse = Network.fuelExpenses.updateFuelExpenseAsync(token, fuelExpenseToUpdate, carId).await()
            ApiResult.apiResultFromResponse(serverResponse)
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    suspend fun saveFuelExpense(fuelExpense: FuelExpense): ApiResult {
        val fuelExpenseToSave = fuelExpense.asNetworkModel()
        val result = saveFuelExpenseOnServer(fuelExpenseToSave, fuelExpense.carId)

        if (result is ApiResult.Success<*>)
            saveFuelExpenseToDatabase((result.data as NetworkFuelExpense).asDatabaseModel(fuelExpense.carId))

        return result
    }

    private suspend fun saveFuelExpenseOnServer(fuelExpenseToSave: NetworkFuelExpense, carId: Long): ApiResult {
        return try {
            val serverResponse = Network.fuelExpenses.saveFuelExpenseAsync(token, fuelExpenseToSave, carId).await()
            ApiResult.apiResultFromResponse(serverResponse)
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    private suspend fun saveFuelExpenseToDatabase(fuelExpense: FuelExpense) {
        withContext(Dispatchers.IO) {
            database.carDao.saveFuelExpenses(fuelExpense)
        }
    }

}