package com.gverse.dcevent.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.repository.FavoriteEventRepository
import com.gverse.dcevent.viewmodel.EventDetailViewModel

class EventDetailViewModelFactory(
    private val eventRepository: EventRepository,
    private val favoriteEventRepository: FavoriteEventRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
            return EventDetailViewModel(eventRepository, favoriteEventRepository) as T
        }
        throw IllegalArgumentException("EventDetailViewModelFactory: Unknown ViewModel class")
    }
}
