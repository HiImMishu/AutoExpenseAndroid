package com.misiak.android.autoexpense.carinformation.engine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misiak.android.autoexpense.repository.EngineRepository
import java.lang.IllegalArgumentException


class SaveOrUpdateEngineViewModelFactory(private val repository: EngineRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveOrUpdateEngineViewModel::class.java))
            return SaveOrUpdateEngineViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}