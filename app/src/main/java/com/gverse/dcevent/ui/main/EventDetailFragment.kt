package com.gverse.dcevent.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gverse.dcevent.R
import com.gverse.dcevent.data.retrofit.ApiClient
import com.gverse.dcevent.databinding.FragmentEventDetailBinding
import com.gverse.dcevent.helper.ImageHelper
import com.gverse.dcevent.repository.EventRepository
import com.gverse.dcevent.repository.FavoriteEventRepository
import com.gverse.dcevent.viewmodel.EventDetailViewModel
import com.gverse.dcevent.viewmodel.factory.EventDetailViewModelFactory

class EventDetailFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailBinding
    private lateinit var viewModel: EventDetailViewModel
    private lateinit var eventRepository: EventRepository
    private lateinit var favoriteEventRepository: FavoriteEventRepository
    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        favoriteEventRepository = FavoriteEventRepository(requireActivity().application)
        eventRepository = EventRepository(ApiClient.apiService)
        val factory = EventDetailViewModelFactory(eventRepository, favoriteEventRepository)
        viewModel = ViewModelProvider(this, factory)[EventDetailViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("eventId")
        if (eventId != null) {
            viewModel.fetchEventDetail(eventId)
        } else {
            Toast.makeText(requireContext(), "Event ID not found", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.appbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.shareBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hey check out this event: ${viewModel.event.value?.name}\n${viewModel.event.value?.link}")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.favBtn.setOnClickListener {
            val ev = viewModel.event.value
            if (ev != null) {
                if (isFavorite) {
                    viewModel.removeFromFavorites(ev.id.toString())
                    updateFavoriteButton()
                    Toast.makeText(context, "Event removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addToFavorites(ev)
                    updateFavoriteButton()
                    Toast.makeText(context, "Event added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }

        favEventHandler(eventId.toString())
        setupFabBehavior()
        observeViewModel()
    }

    private fun favEventHandler(eventId: String) {
        viewModel.checkIsFavorite(eventId).observe(viewLifecycleOwner) { isFav ->
            this.isFavorite = isFav ?: false
            updateFavoriteButton()
        }
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            binding.favBtn.setIconResource(R.drawable.round_favorite_24)
            binding.favBtn.setIconTintResource(R.color.red)
        } else {
            binding.favBtn.setIconResource(R.drawable.baseline_favorite_border_24)
            binding.favBtn.setIconTintResource(R.color.primary_text_color)
        }
    }

    private fun setupFabBehavior() {
        val thresholdInPx = resources.displayMetrics.density * 40
        binding.scrollView.setOnScrollChangeListener { v: View, _: Int, scrollY: Int, _: Int, _: Int ->
            val scrollView = v as ScrollView
            val contentHeight = scrollView.getChildAt(0).measuredHeight
            val scrollViewHeight = scrollView.height

            val distanceToBottom = contentHeight - (scrollY + scrollViewHeight)

            if (distanceToBottom <= thresholdInPx) {
                binding.fab.visibility = View.GONE
            } else {
                binding.fab.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.visibility = View.GONE
    }

    private fun observeViewModel() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let {
                binding.apply {
                    eventName.text = event.name
                    eventSummary.text = event.summary
                    eventLocation.text = event.cityName
                    eventDate.text = event.beginTime
                    eventOwner.text = event.ownerName
                    eventAttendance.text = ((event.quota ?: 0) - (event.registrants ?: 0)).toString()

                    eventDescription.text =
                        event.description?.let { it1 -> HtmlCompat.fromHtml(it1, HtmlCompat.FROM_HTML_MODE_COMPACT) }

                    Glide.with(requireContext())
                        .load(event.imageLogo)
                        .placeholder(R.drawable.noimage)
                        .error(R.drawable.noimage)
                        .into(eventImage)

                    eventImage.setOnClickListener {
                        event.imageLogo?.let { it1 ->
                            ImageHelper.showImageDialog(requireContext(), it1)
                        }
                    }

                    fab.setOnClickListener {
                        event.link?.let { it1 -> openUrl(it1) }
                    }
                    registerBtn.setOnClickListener {
                        event.link?.let { it1 -> openUrl(it1) }
                    }
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                binding.root.visibility = View.GONE
                showErrorDialog(it)
            }
        }
    }
    private fun openUrl(url: String) {
        val openUrlIntent = Intent(Intent.ACTION_VIEW)
        openUrlIntent.data = Uri.parse(url)
        startActivity(openUrlIntent)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.checkIsFavorite(arguments?.getString("eventId").toString()).removeObservers(viewLifecycleOwner)
    }

}