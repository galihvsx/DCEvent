package com.gverse.dcevent.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gverse.dcevent.R
import com.gverse.dcevent.databinding.FragmentFavoriteBinding
import com.gverse.dcevent.repository.FavoriteEventRepository
import com.gverse.dcevent.ui.adapter.RvFavEventAdapter
import com.gverse.dcevent.viewmodel.FavoriteEventViewModel
import com.gverse.dcevent.viewmodel.factory.FavoriteEventViewModelFactory

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favEventAdapter: RvFavEventAdapter
    private lateinit var viewModel: FavoriteEventViewModel
    private lateinit var favEventRepository: FavoriteEventRepository
    private var isDialogShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val view = binding.root

        favEventRepository = FavoriteEventRepository(requireActivity().application)
        val factory = FavoriteEventViewModelFactory(favEventRepository)
        viewModel = ViewModelProvider(this, factory)[FavoriteEventViewModel::class.java]

        binding.topBar.setTitle("Favorite Event")

        setupRecyclerView()
        observeViewModel()

        Log.d("FavFragment", "Hello From Favorite Fragment")
        return view
    }

    private fun observeViewModel() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { eventList ->
            favEventAdapter.updateData(eventList ?: emptyList())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                showNoDataAnim()
            } else {
                hideNoDataAnim()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                if (!isDialogShowing) {
                    isDialogShowing = true
                    showErrorDialog(it) {
                        viewModel.fetchFavoriteEvents()
                    }
                }
            }
        }

        viewModel.fetchFavoriteEvents()
    }


    private fun showNoDataAnim() {
        binding.notFound.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun hideNoDataAnim() {
        binding.notFound.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        favEventAdapter = RvFavEventAdapter(emptyList()) { fav ->
            navigateToEventDetail(fav.id.toInt())
        }
        with(binding) {
            recyclerView.adapter = favEventAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply {
            putInt("eventId", eventId)
        }
        findNavController().navigate(R.id.action_favoriteFragment_to_eventDetailFragment, bundle)
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
}
