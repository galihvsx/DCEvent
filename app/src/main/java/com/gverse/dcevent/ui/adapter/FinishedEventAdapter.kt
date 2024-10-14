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

class FinishedEventAdapter(
    private var finishedEvents: List<Event?>,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<FinishedEventAdapter.FinishedEventViewHolder>() {

    inner class FinishedEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageLogo: ImageView = itemView.findViewById(R.id.imageLogo)
        private val eventName: TextView = itemView.findViewById(R.id.eventName)
        private val eventTime: TextView = itemView.findViewById(R.id.eventTime)

        fun bind(event: Event?) {
            event?.let {
                eventName.text = it.name
                eventTime.text = it.endTime

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
        finishedEvents = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishedEventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return FinishedEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinishedEventViewHolder, position: Int) {
        holder.bind(finishedEvents[position])
    }

    override fun getItemCount(): Int = finishedEvents.size
}
