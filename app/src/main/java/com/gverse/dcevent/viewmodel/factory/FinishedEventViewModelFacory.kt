package com.gverse.dcevent.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.viewmodel.FinishedEventViewModel

class FinishedEventViewModelFacory(private val repository: EventRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FinishedEventViewModel::class.java)) {
            return FinishedEventViewModel(repository) as T
        }
        throw IllegalArgumentException("FinishedEventViewModelFacory: Unknown ViewModel class")
    }
}