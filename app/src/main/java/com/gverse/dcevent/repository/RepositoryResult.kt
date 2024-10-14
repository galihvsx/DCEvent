package com.gverse.dcevent.repository

sealed class RepositoryResult<out T> {
    data class Success<out T>(val data: T) : RepositoryResult<T>()
    data class NetworkError(val message: String) : RepositoryResult<Nothing>()
    data class ApiError(val code: Int, val message: String) : RepositoryResult<Nothing>()
}