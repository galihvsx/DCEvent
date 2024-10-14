package com.gverse.dcevent.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gverse.dcevent.R
import com.gverse.dcevent.database.FavoriteEvent

class RvFavEventAdapter(
    private var favoriteEvent: List<FavoriteEvent>,
    private val onItemClick: (FavoriteEvent) -> Unit
) : RecyclerView.Adapter<RvFavEventAdapter.FavEventViewHolder>() {

    inner class FavEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageLogo: ImageView = itemView.findViewById(R.id.imageLogo)
        private val eventName: TextView = itemView.findViewById(R.id.eventName)

        fun bind(favEvent: FavoriteEvent) {
            eventName.text = favEvent.name

            Glide.with(itemView.context)
                .load(favEvent.mediaCover)
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(imageLogo)

            itemView.setOnClickListener { onItemClick(favEvent) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavEventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_fav_item, parent, false)
        return FavEventViewHolder(view)
    }

    override fun getItemCount(): Int = favoriteEvent.size

    override fun onBindViewHolder(holder: FavEventViewHolder, position: Int) {
        holder.bind(favoriteEvent[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFavoriteEvents: List<FavoriteEvent>) {
        favoriteEvent = newFavoriteEvents
        Log.d("RvFavEventAdapter", "updateData: $favoriteEvent")
        notifyDataSetChanged()
    }
}
