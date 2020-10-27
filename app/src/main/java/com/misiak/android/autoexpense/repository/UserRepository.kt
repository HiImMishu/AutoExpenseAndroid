package com.misiak.android.autoexpense.repository

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.User
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.network.Network
import com.misiak.android.autoexpense.network.dto.NetworkUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val database: AutoExpenseDatabase,
    private val account: GoogleSignInAccount
) {

    suspend fun refreshUser(): ApiResult {
        return try {
            val user = Network.users.getUser("Bearer ${account.idToken!!}").await()
            val result = ApiResult.apiResultFromCode(user.code())
            if (result is ApiResult.Success<*>)
                saveToDatabase(user.body())
            result
        } catch (e: Exception) {
            Log.e(UserRepository::signInUser.toString(), "Error occurred: ${e.message}")
            ApiResult.NetworkError(e)
        }
    }

    suspend fun signInUser(): ApiResult {
        if (checkUserAlreadySignedIn() is ApiResult.Success<*>)
            return ApiResult.Success(Unit)

        return try {
            val signedInUser = Network.users.signInUser("Bearer ${account.idToken!!}").await()
            val result = ApiResult.apiResultFromCode(signedInUser.code())
            if (result is ApiResult.Success<*>) {
                saveToDatabase(signedInUser.body())
            }
            result
        } catch (e: Exception) {
            Log.e(UserRepository::signInUser.toString(), "Error occurred: ${e.message}")
            ApiResult.NetworkError(e)
        }
    }

    private suspend fun saveToDatabase(user: NetworkUser?) {
        withContext(Dispatchers.IO) {
            user?.let { database.userDao.insertUser(userAsDatabaseModel(user)) }
        }
    }

    private suspend fun checkUserAlreadySignedIn(): ApiResult {
        return try {
            val userAlreadySignedIn = Network.users.getUser("Bearer ${account.idToken!!}").await()
            ApiResult.apiResultFromCode(userAlreadySignedIn.code())
        } catch (e: Exception) {
            Log.e(UserRepository::signInUser.toString(), "Error occurred: ${e.message}")
            ApiResult.NetworkError(e)
        }
    }

    private fun userAsDatabaseModel(user: NetworkUser): User {
        return User(
            userId = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            signedAt = user.signedAt
        )
    }
}