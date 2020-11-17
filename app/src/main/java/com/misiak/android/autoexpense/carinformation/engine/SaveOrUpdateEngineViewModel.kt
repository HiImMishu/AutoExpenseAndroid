package com.misiak.android.autoexpense.carinformation.engine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misiak.android.autoexpense.database.entity.Engine
import com.misiak.android.autoexpense.network.ApiResult
import com.misiak.android.autoexpense.repository.EngineRepository
import kotlinx.coroutines.launch

class SaveOrUpdateEngineViewModel(private val repository: EngineRepository) : ViewModel() {

    var engineToSave: LiveData<Engine>? = null

    private var _updateOrSaveCompleted = MutableLiveData<Boolean>(false)
    val updateOrSaveCompleted: LiveData<Boolean>
        get() = _updateOrSaveCompleted

    fun updateOrSaveOperationHandled() {
        _updateOrSaveCompleted.value = false
    }

    fun getEngineFromDatabase(engineId: Long) {
        viewModelScope.launch {
            engineToSave = repository.getEngineById(engineId)
        }
    }

    fun updateEngine(engine: Engine) {

    }

    fun saveEngine(engine: Engine) {
        viewModelScope.launch {
            handleResult(repository.saveEngine(engine))
        }
    }

    private fun handleResult(result: ApiResult) {
        when (result) {
            is ApiResult.Success<*> -> _updateOrSaveCompleted.value = true
            else -> println(result)
            //TODO handle errors
        }
    }
}