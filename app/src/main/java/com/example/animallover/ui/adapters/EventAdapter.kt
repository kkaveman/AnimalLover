package com.example.animallover.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animallover.data.model.Event
import com.example.animallover.R
import com.example.animallover.databinding.ItemEventCardBinding

class EventAdapter(
    private val events: List<Event>)
    : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    // ViewHolder for the regular event items
    inner class EventViewHolder(private val binding : ItemEventCardBinding) : RecyclerView.ViewHolder(binding.root) {
        private val eventImageView: ImageView = binding.imageViewEvent
        private val eventTitleTextView: TextView = binding.textViewEventTitle
        private val eventDescTextView: TextView = binding.textViewEventDesc
        private val eventDateTextView: TextView = binding.textViewEventDate

        fun bind(event: Event) {
            eventTitleTextView.text = event.title
            eventDescTextView.text = event.desc
            eventDateTextView.text = event.date

            Glide.with(itemView.context)
                .load(event.imageResId)
                .into(eventImageView)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}