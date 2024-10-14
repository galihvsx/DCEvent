package com.gverse.dcevent.ui.adapter

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gverse.dcevent.R
import com.gverse.dcevent.data.model.Event
import java.util.Locale

class OngoingEventAdapter(
    private var ongoingEvents: List<Event?>,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<OngoingEventAdapter.OngoingEventViewHolder>() {

    inner class OngoingEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventName: TextView = itemView.findViewById(R.id.eventName)
        private val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
        private val eventTime: TextView = itemView.findViewById(R.id.eventTime)
        private val imageLogo: ImageView = itemView.findViewById(R.id.imageLogo)

        fun bind(event: Event?) {
            event?.let {
                eventName.text = it.name
                eventLocation.text = it.cityName

                it.beginTime?.let { beginTime ->
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                    val date = inputFormat.parse(beginTime)
                    eventTime.text = outputFormat.format(date)
                }

                Glide.with(itemView.context)
                    .load(it.imageLogo)
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                    .into(imageLogo)

                itemView.setOnClickListener { onItemClick(event) }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newEvents: List<Event?>) {
        ongoingEvents = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingEventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return OngoingEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: OngoingEventViewHolder, position: Int) {
        holder.bind(ongoingEvents[position])
    }

    override fun getItemCount(): Int = ongoingEvents.size
}
