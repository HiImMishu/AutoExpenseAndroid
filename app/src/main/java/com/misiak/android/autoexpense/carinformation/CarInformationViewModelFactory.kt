package com.misiak.android.autoexpense.carinformation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.misiak.android.autoexpense.repository.CarRepository
import java.lang.IllegalArgumentException

class CarInformationViewModelFactory(
    private val carId: Long,
    private val repository: CarRepository
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarInformationViewModel::class.java))
            return CarInformationViewModel(carId, repository) as T
        throw throw IllegalArgumentException("Unknown ViewModel class")
    }
}