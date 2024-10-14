package com.gverse.dcevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gverse.dcevent.data.model.Event
import com.gverse.dcevent.data.model.EventResponse
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.repository.RepositoryResult
import kotlinx.coroutines.launch

class OngoingEventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _ongoingEvents = MutableLiveData<List<Event>>(emptyList())
    val ongoingEvents: LiveData<List<Event>> = _ongoingEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    fun clearEvents() {
        _ongoingEvents.value = emptyList()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun fetchOngoingEvents() {
        _isSearching.value = false
        fetchEvents { repository.getUpcomingEvents() }
    }

    fun searchOngoingEvents(query: String) {
        _isSearching.value = true
        fetchEvents { repository.searchOngoingEvents(query) }
    }

    private fun fetchEvents(apiCall: suspend () -> RepositoryResult<EventResponse>) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = apiCall()) {
                is RepositoryResult.Success -> {
                    val events = result.data.listEvents?.filterNotNull() ?: emptyList()
                    _ongoingEvents.value = events
                    _isEmpty.value = events.isEmpty()
                }
                is RepositoryResult.NetworkError -> {
                    _errorMessage.value = "Network error: ${result.message}"
                    _isEmpty.value = true
                }
                is RepositoryResult.ApiError -> {
                    Log.e("OngoingEventViewModel", "API Error: ${result.code} - ${result.message}")
                    _errorMessage.value = "API error: ${result.code} - ${result.message}"
                    _isEmpty.value = true
                }
            }

            _isLoading.value = false
            _isSearching.value = false
        }
    }
}
