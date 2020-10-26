package com.misiak.android.autoexpense.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.misiak.android.autoexpense.repository.CarRepository
import java.lang.IllegalArgumentException

class MainScreeViewModelFactory(
    private val repository: CarRepository
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java))
            return MainScreenViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}