package com.gverse.dcevent.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.gverse.dcevent.R
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.data.retrofit.ApiClient
import com.gverse.dcevent.databinding.FragmentOngoingEventBinding
import com.gverse.dcevent.ui.adapter.OngoingEventAdapter
import com.gverse.dcevent.viewmodel.OngoingEventViewModel
import com.gverse.dcevent.viewmodel.factory.OngoingEventViewModelFactory

class OngoingEventFragment : Fragment() {

    private lateinit var binding: FragmentOngoingEventBinding
    private lateinit var eventAdapter: OngoingEventAdapter
    private lateinit var viewModel: OngoingEventViewModel
    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView

    private lateinit var eventRepository: EventRepository

    private var isDialogShowing = false
    private lateinit var notFoundView : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOngoingEventBinding.inflate(inflater, container, false)
        val view = binding.root
        notFoundView = binding.notFound

        eventRepository = EventRepository(ApiClient.apiService)
        val factory =  OngoingEventViewModelFactory(eventRepository)
        viewModel = ViewModelProvider(this, factory)[OngoingEventViewModel::class.java]


        setupRecyclerView()
        observeViewModel()
        setupSearch()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchBar.clearText()
            viewModel.fetchOngoingEvents()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    private fun observeViewModel() {
        viewModel.ongoingEvents.observe(viewLifecycleOwner) { eventList ->
            eventAdapter.updateData(eventList ?: emptyList())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                showNoDataAnim()
            } else {
                hideNoDataAnim()
            }
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            if (isSearching) {
                binding.recyclerViewVertical.visibility = View.GONE
                binding.notFound.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if (!isDialogShowing) {
                    isDialogShowing = true
                    showErrorDialog(it) {
                        viewModel.fetchOngoingEvents()
                    }
                    viewModel.clearErrorMessage()
                }
            }
        }

        viewModel.fetchOngoingEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = OngoingEventAdapter(emptyList()) { event ->
            navigateToEventDetail(event.id!!)
        }
        with(binding) {
            recyclerViewVertical.adapter = eventAdapter
            recyclerViewVertical.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(
            R.id.action_ongoingEventFragment_to_eventDetailFragment,
            bundle
        )
    }

    private fun showNoDataAnim() {

        notFoundView.visibility = View.VISIBLE
        binding.recyclerViewVertical.visibility = View.GONE
    }

    private fun hideNoDataAnim() {
        notFoundView.visibility = View.GONE
        binding.recyclerViewVertical.visibility = View.VISIBLE
    }

    private fun showErrorDialog(message: String, onRetry: () -> Unit) {
        AlertDialog.Builder(requireContext())
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
            .show()
    }


    private fun setupSearch() {
        searchBar = binding.searchBar
        searchView = binding.searchView
        searchView.hint = getString(R.string.search_event)
        searchView.setupWithSearchBar(searchBar)

        searchView.editText.setOnEditorActionListener { v, _, _ ->
            val query = v.text.toString().trim()
            searchBar.setText(query)
            searchView.hide()
            viewModel.clearEvents()

            if (query.isEmpty()) {
                viewModel.fetchOngoingEvents()
            } else {
                viewModel.searchOngoingEvents(query)
            }
            true
        }
    }
}
