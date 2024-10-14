package com.gverse.dcevent.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gverse.dcevent.helper.SettingPreferences
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.viewmodel.HomeViewModel

class HomeViewModelFactory(private val repository: EventRepository, private val pref: SettingPreferences) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, pref) as T
        }
        throw IllegalArgumentException("HomeViewModelFactory: Unknown ViewModel class")
    }
}