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

class EventHomeAdapter(
    private val events: List<Event>,
    private val onSeeAllClicked: () -> Unit // Listener for the button click
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_EVENT = 0
        private const val VIEW_TYPE_BUTTON = 1
    }

    // ViewHolder for the regular event items
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventImageView: ImageView = itemView.findViewById(R.id.eventImageView)
        private val eventTitleTextView: TextView = itemView.findViewById(R.id.eventTitleTextView)

        fun bind(event: Event) {
            eventTitleTextView.text = event.title
            Glide.with(itemView.context)
                .load(event.imageResId)
                .into(eventImageView)
        }
    }

    // ViewHolder for the "View More" text
    class SeeAllViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val viewMoreTextView: TextView = itemView.findViewById(R.id.tv_view_more)
    }

    override fun getItemViewType(position: Int): Int {
        // If the position is within the bounds of the events list, it's an event.
        // Otherwise, it's the "View More" button at the end.
        return if (position < events.size) {
            VIEW_TYPE_EVENT
        } else {
            VIEW_TYPE_BUTTON
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EVENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_card_home, parent, false)
                EventViewHolder(view)
            }
            VIEW_TYPE_BUTTON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_see_all_events, parent, false)
                SeeAllViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_EVENT -> {
                (holder as EventViewHolder).bind(events[position])
            }
            VIEW_TYPE_BUTTON -> {
                (holder as SeeAllViewHolder).viewMoreTextView.setOnClickListener {
                    onSeeAllClicked()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        // Total items are the number of events plus one for the "View More" button.
        return if (events.isNotEmpty()) events.size + 1 else 0
    }
}