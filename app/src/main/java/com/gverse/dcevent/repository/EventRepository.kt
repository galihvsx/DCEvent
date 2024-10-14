package com.gverse.dcevent.repository

import com.gverse.dcevent.data.model.EventDetailResponse
import com.gverse.dcevent.data.model.EventResponse
import com.gverse.dcevent.data.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

class EventRepository(private val apiService: ApiService) {
    suspend fun getFinishedEvents(limit: Int? = null): RepositoryResult<EventResponse> {
        return try {
            val response = apiService.getEvents(active = 0,limit = limit)
            RepositoryResult.Success(response)
        } catch (e: IOException) {
            RepositoryResult.NetworkError("No internet connection")
        } catch (e: HttpException) {
            RepositoryResult.ApiError(e.code(), e.message())
        }
    }

    suspend fun getUpcomingEvents(limit: Int? = null): RepositoryResult<EventResponse> {
        return try {
            val response = apiService.getEvents(active = 1, limit = limit)
            RepositoryResult.Success(response)
        } catch (e: IOException) {
            RepositoryResult.NetworkError("No internet connection")
        } catch (e: HttpException) {
            RepositoryResult.ApiError(e.code(), e.message())
        }
    }

    suspend fun getEventDetail(eventId: Int): RepositoryResult<EventDetailResponse> {
        return try {
            val response = apiService.getEventDetail(eventId)
            RepositoryResult.Success(response)
        } catch (e: IOException) {
            RepositoryResult.NetworkError("No internet connection")
        } catch (e: HttpException) {
            RepositoryResult.ApiError(e.code(), e.message())
        }
    }

    suspend fun searchOngoingEvents(query: String, limit: Int? = null): RepositoryResult<EventResponse> {
        return try {
            val response = apiService.searchEvents(query, active = 1 , limit = limit)
            RepositoryResult.Success(response)
        } catch (e: IOException) {
            RepositoryResult.NetworkError("No internet connection")
        } catch (e: HttpException) {
            RepositoryResult.ApiError(e.code(), e.message())
        }
    }

    suspend fun searchFinishedEvents(query: String, limit: Int? = null): RepositoryResult<EventResponse> {
        return try {
            val response = apiService.searchEvents(query, active = 0, limit = limit)
            RepositoryResult.Success(response)
        } catch (e: IOException) {
            RepositoryResult.NetworkError("No internet connection")
        } catch (e: HttpException) {
            RepositoryResult.ApiError(e.code(), e.message())
        }
    }
}
