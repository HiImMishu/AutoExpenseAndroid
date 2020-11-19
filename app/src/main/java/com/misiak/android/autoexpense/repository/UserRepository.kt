package com.misiak.android.autoexpense.repository

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
    account: GoogleSignInAccount
) {

    private var token = "Bearer ${account.idToken!!}"

    fun updateToken(account: GoogleSignInAccount) {
        token = "Bearer ${account.idToken!!}"
    }

    suspend fun signInUser(): ApiResult {
        val userAlreadySignedInResult = checkUserAlreadySignedIn()

        return if (isSuccess(userAlreadySignedInResult))
            userAlreadySignedInResult
        else try {
            val serverResponse = Network.users.signInUserAsync(token).await()
            val result = ApiResult.apiResultFromResponse(serverResponse)
            if (isSuccess(result)) {
                saveToDatabase(serverResponse.body())
            }
            result
        } catch (e: Exception) {
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
            val serverResponse = Network.users.getUserAsync(token).await()
            ApiResult.apiResultFromResponse(serverResponse)
        } catch (e: Exception) {
            ApiResult.NetworkError(e)
        }
    }

    private fun isSuccess(result: ApiResult) =
        result is ApiResult.Success<*>

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

//UNUSED
//    suspend fun refreshUser(): ApiResult {
//        return try {
//            val user = Network.users.getUser(token).await()
//            val result = ApiResult.apiResultFromCode(user.code())
//            if (result is ApiResult.Success<*>)
//                saveToDatabase(user.body())
//            result
//        } catch (e: Exception) {
//            ApiResult.NetworkError(e)
//        }
//    }
