package com.misiak.android.autoexpense.carinformation.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misiak.android.autoexpense.database.entity.Car
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.repository.CarRepository
import kotlinx.coroutines.launch
import java.io.File

class PhotoFragmentViewModel(private val carRepository: CarRepository, private val carId: Long) : ViewModel() {

    val car: LiveData<Car> = carRepository.getCarById(carId)

    private val _takePhoto = MutableLiveData<Boolean>(false)
    val takePhoto: LiveData<Boolean>
        get() = _takePhoto

    fun takePhotoClicked() {
        _takePhoto.value = true
    }

    fun doneTakingPhoto() {
        _takePhoto.value = false
    }

    private val _connectionError = MutableLiveData<Boolean>(false)
    val connectionError: LiveData<Boolean>
        get() = _connectionError

    fun connectionErrorHandled() {
        _connectionError.value = false
    }

    private var _unknownError = MutableLiveData<Boolean>(false)
    val unknownError: LiveData<Boolean>
        get() = _unknownError

    fun unknownErrorHandled() {
        _unknownError.value = false
    }

    private val _updateSuccess = MutableLiveData<Boolean>(false)
    val updateSuccess: LiveData<Boolean>
        get() = _updateSuccess

    fun navigatedOnSuccess() {
        _updateSuccess.value = false
    }

    private val _tokenExpired = MutableLiveData<Boolean>(false)
    val tokenExpired: LiveData<Boolean>
        get() = _tokenExpired

    fun expiredTokenHandled() {
        _tokenExpired.value = false
    }

    private fun handleOperationResult(result: ApiResult) {
        when (result) {
            is ApiResult.NetworkError -> _connectionError.value = true
            is ApiResult.AuthenticationError -> _tokenExpired.value = true
            is ApiResult.Success<*> -> _updateSuccess.value = true
            else -> _unknownError.value = true
        }
    }

    fun saveFileFromUri(fileName: String, outputDirectory: File) {
        car.value?.let {
            it.photoUrl?.let {
                val prevFile = File(outputDirectory, it)
                if (prevFile.exists()) {
                    prevFile.delete()
                }
            }
            val file = File(outputDirectory, fileName)
            val newCar = it.copy(photoUrl = fileName)
            if (file.exists()) {
                viewModelScope.launch {
                    val result = carRepository.updateCar(newCar)
                    if (result is ApiResult.NetworkError) {
                        file.delete()
                    }
                    handleOperationResult(result)
                }
            }
        }
    }
}