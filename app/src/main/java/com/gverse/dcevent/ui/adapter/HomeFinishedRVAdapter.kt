package com.gverse.dcevent.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gverse.dcevent.R
import com.gverse.dcevent.data.model.Event

class HomeFinishedRVAdapter(
    private val finishedEvents: MutableList<Event?>,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<HomeFinishedRVAdapter.FinishedEventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishedEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_home_item, parent, false)
        return FinishedEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinishedEventViewHolder, position: Int) {
        finishedEvents[position]?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = finishedEvents.size

    inner class FinishedEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageLogo: ImageView = itemView.findViewById(R.id.rvImageLogo)
        private val eventName: TextView = itemView.findViewById(R.id.txEventName)
        private val eventTime: TextView = itemView.findViewById(R.id.txEventDate)
        private val eventOwner: TextView = itemView.findViewById(R.id.txEventOwner)
        private val eventLocation: TextView = itemView.findViewById(R.id.txEventLocation)

        fun bind(event: Event) {
            eventName.text = event.name
            eventTime.text = event.endTime
            eventOwner.text = event.ownerName
            eventLocation.text = event.cityName

            Glide.with(itemView.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.noimage)
                .into(imageLogo)

            itemView.setOnClickListener { onItemClick(event) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFinishedEvents: List<Event?>) {
        finishedEvents.clear()
        finishedEvents.addAll(newFinishedEvents)
        notifyDataSetChanged()
    }
}