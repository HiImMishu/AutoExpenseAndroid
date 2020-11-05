package com.misiak.android.autoexpense.network

import com.misiak.android.autoexpense.network.dto.NetworkCar
import retrofit2.Response
import kotlin.Exception

sealed class ApiResult{
    data class Success<out T: Any?>(val data: T) : ApiResult()
    data class NetworkError(val exception: Exception) : ApiResult()
    object ServerError : ApiResult()
    object AuthenticationError : ApiResult()
    object ResourceNotFoundError: ApiResult()
    object UnknownError: ApiResult()

    companion object {
        fun apiResultFromResponse(response: Response<*>): ApiResult {
            return when (response.code()) {
                200 -> Success(response.body())
                500 -> ServerError
                404 -> ResourceNotFoundError
                401 -> AuthenticationError
                else -> UnknownError
            }
        }
    }
}