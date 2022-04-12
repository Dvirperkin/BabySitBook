package com.example.babysitbook.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.CalendarMainBinding
import com.example.babysitbook.databinding.EditEventBinding
import com.example.babysitbook.model.CalendarEvent
import com.example.babysitbook.model.CalendarEventAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CalendarMainFragment : Fragment(){
    private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    private val eventsRef = database.getReference("Calendar/Events")

    private lateinit var binding: CalendarMainBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: CalendarEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CalendarMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addEventButton.setOnClickListener {
            val action = CalendarMainFragmentDirections.actionCalendarMainFragmentToEditEventFragment()
            findNavController().navigate(action)
        }

        val options = FirebaseRecyclerOptions.Builder<CalendarEvent>()
            .setQuery(eventsRef, CalendarEvent::class.java)
            .build()
        adapter = CalendarEventAdapter(options)
        manager = LinearLayoutManager(context)
        manager.stackFromEnd = false

        binding.eventRecyclerView.layoutManager = manager
        binding.eventRecyclerView.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
}