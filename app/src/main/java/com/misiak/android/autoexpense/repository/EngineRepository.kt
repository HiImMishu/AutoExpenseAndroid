package com.misiak.android.autoexpense.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.database.entity.asNetworkEngine
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.network.Network
import com.misiak.android.autoexpense.network.dto.NetworkEngine
import com.misiak.android.autoexpense.network.dto.asDatabaseEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EngineRepository(
    private val database: AutoExpenseDatabase,
    var account: GoogleSignInAccount
) {

    private val token = "Bearer ${account.idToken!!}"

    fun getEngineById(engineId: Long): LiveData<Engine> {
        return database.carDao.getEngineByIdAsync(engineId)
    }

    suspend fun saveEngine(engine: Engine): ApiResult {
        val engineToUpdate = engine.asNetworkEngine()
        val result = saveEngineOnServer(engineToUpdate, engine.carId)

        if (result is ApiResult.Success<*>)
            saveEngineToDatabase((result.data as NetworkEngine).asDatabaseEngine(engine.carId))

        return result
    }

    private suspend fun saveEngineOnServer(engine: NetworkEngine, carId: Long): ApiResult {
        return try {
            val serverResponse = Network.engines.saveEngineAsync(token, engine, carId).await()
            ApiResult.apiResultFromResponse(serverResponse)
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    private suspend fun saveEngineToDatabase(engine: Engine) {
        withContext(Dispatchers.IO) {
            database.carDao.updateCarEngineId(engine.carId, engine.id)
            database.carDao.saveEngines(engine)
        }
    }

}