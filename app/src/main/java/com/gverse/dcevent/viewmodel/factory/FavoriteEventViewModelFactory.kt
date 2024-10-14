package com.gverse.dcevent.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gverse.dcevent.repository.FavoriteEventRepository
import com.gverse.dcevent.viewmodel.FavoriteEventViewModel

class FavoriteEventViewModelFactory(private val repository: FavoriteEventRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteEventViewModel::class.java)) {
            return FavoriteEventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}