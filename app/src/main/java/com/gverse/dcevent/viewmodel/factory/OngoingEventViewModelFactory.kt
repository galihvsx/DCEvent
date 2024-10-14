package com.gverse.dcevent.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.viewmodel.OngoingEventViewModel

class OngoingEventViewModelFactory(private val repository: EventRepository): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(OngoingEventViewModel::class.java)) {
            return OngoingEventViewModel(repository) as T
        }
        throw IllegalArgumentException("OngoingEventViewModelFacory: Unknown ViewModel class")
    }
}