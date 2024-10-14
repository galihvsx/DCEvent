package com.gverse.dcevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gverse.dcevent.data.model.Event
import com.gverse.dcevent.database.FavoriteEvent
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.repository.FavoriteEventRepository
import com.gverse.dcevent.repository.RepositoryResult
import kotlinx.coroutines.launch

@Suppress("unused")
class EventDetailViewModel(
    private val eventRepository: EventRepository,
    private val favoriteEventRepository: FavoriteEventRepository
) : ViewModel() {

    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getFavoriteEventById(eventId: String): LiveData<FavoriteEvent> = favoriteEventRepository.getFavoriteEventById(eventId)

    fun checkIsFavorite(eventId: String): LiveData<Boolean> = favoriteEventRepository.isFavorite(eventId)

    fun addToFavorites(event: Event) {
        viewModelScope.launch {
            val favoriteEvent = FavoriteEvent(
                id = event.id.toString(),
                name = event.name?: "-",
                mediaCover = event.imageLogo,
            )
            favoriteEventRepository.insert(favoriteEvent)
        }
    }

    fun removeFromFavorites(eventId: String) {
        viewModelScope.launch {
                favoriteEventRepository.delete(eventId)
        }
    }

    fun fetchEventDetail(eventId: Int) {
        if (_event.value != null) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = eventRepository.getEventDetail(eventId)) {
                is RepositoryResult.Success -> {
                    _event.value = result.data.event
                }
                is RepositoryResult.NetworkError -> {
                    _errorMessage.value = "Network error: ${result.message}"
                }
                is RepositoryResult.ApiError -> {
                    Log.e("EventDetailViewModel", "API Error: ${result.code} - ${result.message}")
                    _errorMessage.value = "API error: ${result.code} - ${result.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun clearEvent() {
        _event.value = null
    }
}
