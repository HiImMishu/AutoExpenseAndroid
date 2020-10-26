package com.misiak.android.autoexpense.network

import kotlin.Exception

sealed class ApiResult{
    data class Success<out T: Any>(val data: T) : ApiResult()
    data class NetworkError(val exception: Exception) : ApiResult()
    object ServerError : ApiResult()
    object AuthenticationError : ApiResult()
    object ResourceNotFoundError: ApiResult()
    object UnknownError: ApiResult()

    companion object {
        fun apiResultFromCode(code: Int): ApiResult {
            return when (code) {
                200 -> ApiResult.Success(Unit)
                500 -> ApiResult.ServerError
                404 -> ApiResult.ResourceNotFoundError
                401 -> ApiResult.AuthenticationError
                else -> ApiResult.UnknownError
            }
        }
    }
}