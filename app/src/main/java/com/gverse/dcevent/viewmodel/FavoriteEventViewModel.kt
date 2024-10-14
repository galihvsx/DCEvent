package com.gverse.dcevent.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gverse.dcevent.database.FavoriteEvent
import com.gverse.dcevent.repository.FavoriteEventRepository

@Suppress("CanBeParameter")
class FavoriteEventViewModel(private val repository: FavoriteEventRepository) : ViewModel() {

    val favoriteEvents: LiveData<List<FavoriteEvent>> = repository.getAllFavorites()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    fun fetchFavoriteEvents() {
        _isLoading.value = true
        favoriteEvents.observeForever { events ->
            _isEmpty.value = events.isNullOrEmpty()
            _isLoading.value = false
        }
    }
}

