package com.example.babysitbook.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.EventBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

class CalendarEventAdapter(
    private val options: FirestoreRecyclerOptions<CalendarEvent>
    ) : FirestoreRecyclerAdapter<CalendarEvent, RecyclerView.ViewHolder>(options)
{
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.event, parent, false)
        val binding = EventBinding.bind(view)
        return EventViewHolder(binding, mListener)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: CalendarEvent
    ) {
        (holder as EventViewHolder).bind(model)
    }

    inner class EventViewHolder(private val binding: EventBinding, listener: OnItemClickListener)
        : RecyclerView.ViewHolder(binding.root){
        init{
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

        fun bind(item : CalendarEvent){
            binding.eventTextView.text = item.title
            binding.startTimeTextView.text = item.startTime
            binding.endTimeTextView.text = item.endTime
        }
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        println(e)
    }
}