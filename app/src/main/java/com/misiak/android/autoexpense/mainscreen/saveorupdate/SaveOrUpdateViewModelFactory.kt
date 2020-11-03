package com.misiak.android.autoexpense.mainscreen.saveorupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misiak.android.autoexpense.repository.CarRepository
import java.lang.IllegalArgumentException

class SaveOrUpdateViewModelFactory(val repository: CarRepository): ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveOrUpdateViewModel::class.java))
            return SaveOrUpdateViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}