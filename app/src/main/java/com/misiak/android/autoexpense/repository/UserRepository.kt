package com.misiak.android.autoexpense.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.database.AutoExpenseDatabase
import com.misiak.android.autoexpense.database.entity.User
import com.misiak.android.autoexpense.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val database: AutoExpenseDatabase, private val account: GoogleSignInAccount) {

    suspend fun refreshUser() {
        withContext(Dispatchers.IO) {
            try {
                val user = Network.users.getUser("Bearer ${account.idToken!!}").await()
            } catch (e: Exception) {
                println(e.message + e::class.java.toString())
            }

            //database.userDao.insertUser(user)
        }
    }

    suspend fun signInUser() {
        return
        withContext(Dispatchers.IO) {
            val userAlreadySignedIn = Network.users.getUser("Bearer ${account.idToken!!}").await()
            if (userAlreadySignedIn.code() == 404) {
                val signedInUser = Network.users.signInUser("Bearer ${account.idToken!!}").await()
                println(signedInUser.body())
                //if (signedInUser.code() == 200)
                //database.userDao.insertUser(signedInUser.body()!!)

                //TODO throw exception on error
            }
            else
                refreshUser()
        }
    }
}