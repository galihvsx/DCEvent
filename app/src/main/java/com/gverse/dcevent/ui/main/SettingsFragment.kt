package com.gverse.dcevent.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gverse.dcevent.databinding.FragmentSettingsBinding
import com.gverse.dcevent.helper.SettingPreferences
import com.gverse.dcevent.helper.dataStore
import com.gverse.dcevent.viewmodel.SettingsViewModel
import com.gverse.dcevent.viewmodel.factory.SettingsViewModelFactory

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val pref = SettingPreferences.getInstance(requireActivity().application.dataStore)

        viewModel = ViewModelProvider(this, SettingsViewModelFactory(pref))[SettingsViewModel::class.java]

        observeViewModel()
        setupSwitch()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if(isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchDm.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchDm.isChecked = false
            }
        }
    }

    private fun setupSwitch() {
        binding.switchDm.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }
    }
}