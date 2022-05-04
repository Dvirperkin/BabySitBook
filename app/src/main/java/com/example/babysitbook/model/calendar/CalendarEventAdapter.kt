package com.example.babysitbook.model.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.EventBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class CalendarEventAdapter(
    private val options: FirebaseRecyclerOptions<CalendarEvent>
) : FirebaseRecyclerAdapter<CalendarEvent, RecyclerView.ViewHolder>(options)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.event, parent, false)
        val binding = EventBinding.bind(view)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: CalendarEvent
    ) {
        (holder as EventViewHolder).bind(model)
    }

    inner class EventViewHolder(private val binding: EventBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : CalendarEvent){
            binding.eventTextView.text = item.title
            binding.startTimeTextView.text = item.startTime
            binding.endTimeTextView.text = item.endTime
        }
    }
}