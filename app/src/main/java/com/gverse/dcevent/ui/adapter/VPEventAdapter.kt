package com.gverse.dcevent.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gverse.dcevent.R
import com.gverse.dcevent.data.model.Event

class VPEventAdapter(
    private var eventList: MutableList<Event?>,
    private val onItemClick: (Event?) -> Unit
) : RecyclerView.Adapter<VPEventAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val imageView: ImageView) :
        RecyclerView.ViewHolder(imageView) {
        fun bind(event: Event?) {
            Glide.with(imageView.context)
                .load(event?.imageLogo)
                .placeholder(R.drawable.noimage)
                .into(imageView)

            imageView.setOnClickListener {
                event?.let { onItemClick(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vp, parent, false) as ImageView
        return EventViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newEventList: List<Event?>) {
        eventList.clear()
        eventList.addAll(newEventList)
        notifyDataSetChanged()
    }
}
