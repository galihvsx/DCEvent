package com.gverse.dcevent.data.retrofit

import com.gverse.dcevent.data.model.EventDetailResponse
import com.gverse.dcevent.data.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int,
        @Query("limit") limit: Int? = null
    ): EventResponse

    @GET("events")
    suspend fun searchEvents(
        @Query("q") query: String,
        @Query("active") active: Int,
        @Query("limit") limit: Int? = null
    ): EventResponse

    @GET("events/{id}")
    suspend fun getEventDetail(@Path("id") eventId: Int): EventDetailResponse
}
