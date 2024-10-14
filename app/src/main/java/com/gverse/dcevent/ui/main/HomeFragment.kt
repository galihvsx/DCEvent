package com.gverse.dcevent.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gverse.dcevent.R
import com.gverse.dcevent.databinding.FragmentHomeBinding
import com.gverse.dcevent.ui.adapter.HomeFinishedRVAdapter
import com.gverse.dcevent.ui.adapter.VPEventAdapter
import com.gverse.dcevent.viewmodel.HomeViewModel
import com.gverse.dcevent.viewmodel.factory.HomeViewModelFactory
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.data.retrofit.ApiClient
import com.gverse.dcevent.helper.SettingPreferences
import com.gverse.dcevent.helper.dataStore
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeFinishedRVAdapter: HomeFinishedRVAdapter
    private lateinit var vpEventAdapter: VPEventAdapter
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var eventRepository: EventRepository

    private val handler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null
    private var currentPage = 0
    private var isDialogShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        eventRepository = EventRepository(ApiClient.apiService)
        val pref = SettingPreferences.getInstance(requireActivity().application.dataStore)
        val factory = HomeViewModelFactory(eventRepository, pref)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setupViewPager()
        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setupViewPager() {
        vpEventAdapter = VPEventAdapter(mutableListOf()) { event ->
            event?.id?.let { navigateToEventDetail(it) }
        }

        binding.vpPopularCourse.adapter = vpEventAdapter
        dotsIndicator = binding.dotsIndicator
    }

    private fun setupRecyclerView() {
        homeFinishedRVAdapter = HomeFinishedRVAdapter(mutableListOf()) { event ->
            navigateToEventDetail(event.id!!)
        }

        binding.rvFinishedCourse.adapter = homeFinishedRVAdapter
        binding.rvFinishedCourse.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun startAutoScroll(itemCount: Int) {
        stopAutoScroll()

        autoScrollRunnable = Runnable {
            if (currentPage == itemCount) {
                currentPage = 0
            }
            binding.vpPopularCourse.setCurrentItem(currentPage++, true)
            handler.postDelayed(autoScrollRunnable!!, 3000)
        }

        handler.postDelayed(autoScrollRunnable!!, 3000)
    }

    private fun stopAutoScroll() {
        autoScrollRunnable?.let { handler.removeCallbacks(it) }
    }

    @SuppressLint("CommitTransaction")
    private fun observeViewModel() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { eventList ->
            eventList?.let {
                vpEventAdapter.updateData(it)
                dotsIndicator.attachTo(binding.vpPopularCourse)
                startAutoScroll(it.size)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { finishedEvents ->
            finishedEvents?.let {
                homeFinishedRVAdapter.updateData(it)
            }
        }

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if (!isDialogShowing) {
                    isDialogShowing = true
                    showErrorDialog(it) {
                        viewModel.fetchFinishedEvents()
                        viewModel.fetchUpcomingEvents()
                    }
                    viewModel.clearErrorMessage()
                }
            }
        }
        viewModel.fetchFinishedEvents()
        viewModel.fetchUpcomingEvents()
    }

    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(R.id.action_homeFragment_to_eventDetailFragment, bundle)
    }

    private fun showErrorDialog(message: String, onRetry: () -> Unit) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Retry") { _, _ ->
                onRetry()
                isDialogShowing = false
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                isDialogShowing = false
            }
            .create()

        dialog.setOnDismissListener {
            isDialogShowing = false
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
    }
}
