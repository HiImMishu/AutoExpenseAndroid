package com.misiak.android.autoexpense.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.repository.CarRepository
import com.misiak.android.autoexpense.repository.UserRepository
import java.lang.IllegalArgumentException

class SignInViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}