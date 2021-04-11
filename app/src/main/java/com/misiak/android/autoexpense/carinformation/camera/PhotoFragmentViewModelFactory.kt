package com.misiak.android.autoexpense.carinformation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misiak.android.autoexpense.repository.CarRepository

class PhotoFragmentViewModelFactory(private val repository: CarRepository, private val carId: Long) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoFragmentViewModel::class.java))
            return PhotoFragmentViewModel(repository, carId) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}