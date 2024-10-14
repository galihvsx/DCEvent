package com.gverse.dcevent.viewmodel

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gverse.dcevent.data.model.Event
import com.gverse.dcevent.data.model.EventResponse
import com.gverse.dcevent.helper.SettingPreferences
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.repository.RepositoryResult
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel(private val repository: EventRepository, private val pref: SettingPreferences) : ViewModel() {
    private val _upcomingEvents = MutableLiveData<List<Event?>>()
    val upcomingEvents: LiveData<List<Event?>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<Event?>>()
    val finishedEvents: LiveData<List<Event?>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchUpcomingEvents(forceReload: Boolean = false) {
        fetchEvents(_upcomingEvents, forceReload) { repository.getUpcomingEvents(limit = 5) }
    }

    fun fetchFinishedEvents(forceReload: Boolean = false) {
        fetchEvents(_finishedEvents, forceReload) { repository.getFinishedEvents(limit = 5) }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }


    private fun fetchEvents(
        eventsLiveData: MutableLiveData<List<Event?>>,
        forceReload: Boolean = false,
        apiCall: suspend () -> RepositoryResult<EventResponse>
    ) {
        if (eventsLiveData.value.isNullOrEmpty() || forceReload) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null
                when (val result = apiCall()) {
                    is RepositoryResult.Success -> {
                        eventsLiveData.value = result.data.listEvents?.sortedByDescending { event ->
                            event?.beginTime?.let {
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it)
                            }
                        }
                    }
                    is RepositoryResult.NetworkError -> _errorMessage.value = result.message
                    is RepositoryResult.ApiError -> {
                        Log.d("ApiError", "ApiError: ${result.code} | ${result.message}")
                        _errorMessage.value = result.message
                    }
                }
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
