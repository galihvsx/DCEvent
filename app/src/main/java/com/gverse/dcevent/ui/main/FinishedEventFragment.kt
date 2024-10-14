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
import com.gverse.dcevent.R
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.data.retrofit.ApiClient
import com.gverse.dcevent.databinding.FragmentFinishedEventBinding
import com.gverse.dcevent.ui.adapter.FinishedEventAdapter
import com.gverse.dcevent.viewmodel.FinishedEventViewModel
import com.gverse.dcevent.viewmodel.factory.FinishedEventViewModelFacory

class FinishedEventFragment : Fragment() {

    private lateinit var binding: FragmentFinishedEventBinding
    private lateinit var viewModel: FinishedEventViewModel
    private lateinit var finishedEventAdapter: FinishedEventAdapter
    private lateinit var eventRepository: EventRepository
    private lateinit var searchView: com.google.android.material.search.SearchView
    private lateinit var searchBar: SearchBar
    private lateinit var notFoundView: LinearLayout

    private var isDialogShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        val view = binding.root

        eventRepository = EventRepository(ApiClient.apiService)
        val factory = FinishedEventViewModelFacory(eventRepository)
        viewModel = ViewModelProvider(this, factory)[FinishedEventViewModel::class.java]
        notFoundView = binding.notFound

        setupRecyclerView()
        observeViewModel()
        setupSearch()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            binding.searchBar.clearText()
            viewModel.fetchFinishedEvents()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return view
    }

    private fun observeViewModel() {
        viewModel.finishedEvents.observe(viewLifecycleOwner) { eventList ->
            finishedEventAdapter.updateData(eventList ?: emptyList())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) {isEmpty ->
            if(isEmpty) {
                showNoDataAnim()
            } else {
                hideNoDataAnim()
            }
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            if (isSearching) {
                binding.recyclerViewVertical2.visibility = View.GONE
                binding.notFound.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if(!isDialogShowing) {
                    isDialogShowing = true
                    showErrorDialog(it) {
                        viewModel.fetchFinishedEvents()
                    }
                    viewModel.clearErrorMessage()
                }
            }
        }

        viewModel.fetchFinishedEvents()
    }

    private fun setupRecyclerView() {
        finishedEventAdapter = FinishedEventAdapter(emptyList()) { event ->
            navigateToEventDetail(event.id!!)
        }
        with(binding) {
            recyclerViewVertical2.adapter = finishedEventAdapter
            recyclerViewVertical2.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(R.id.action_finishedEventFragment_to_eventDetailFragment, bundle)
    }

    private fun showNoDataAnim() {

        notFoundView.visibility = View.VISIBLE
        binding.recyclerViewVertical2.visibility = View.GONE
    }

    private fun hideNoDataAnim() {
        notFoundView.visibility = View.GONE
        binding.recyclerViewVertical2.visibility = View.VISIBLE
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
                viewModel.fetchFinishedEvents()
            } else {
                viewModel.searchFinishedEvents(query)
            }
            true
        }
    }

}
