package com.example.babysitbook.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.CalendarMainBinding
import com.example.babysitbook.model.CalendarEvent
import com.example.babysitbook.model.CalendarEventAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.util.*

class CalendarMainFragment : Fragment(){

    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: CalendarMainBinding
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapter: CalendarEventAdapter
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var date: String
    private lateinit var query: Query

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
        firestore = Firebase.firestore
        functions = Firebase.functions
        auth = Firebase.auth

        val calendar = Calendar.getInstance()
        binding.calendarView2.date = calendar.timeInMillis
        dateStringify(calendar)

        binding.calendarView2.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(year,month,dayOfMonth)
            binding.calendarView2.date = calendar.timeInMillis
            dateStringify(calendar)

            adapter.stopListening()
            setRecyclerView()
            adapter.startListening()
        }

        binding.addEventButton.setOnClickListener {
            dateStringify(calendar)
            setFragmentResult("passSelectedDate", bundleOf("selectedDate" to date))
            val action = CalendarMainFragmentDirections.actionCalendarMainFragmentToEditEventFragment()
            findNavController().navigate(action)
        }
        setRecyclerView()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    private fun dateStringify(calendar: Calendar) {
        calendar.timeInMillis = binding.calendarView2.date
        val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT)
        date = dateFormatter.format(binding.calendarView2.date)
    }

    private fun setRecyclerView() {
        query = firestore.collection("calendar").document(auth.currentUser!!.uid)
            .collection("events").whereEqualTo("date", date).orderBy("startTime")

        val options = FirestoreRecyclerOptions.Builder<CalendarEvent>()
            .setQuery(query, CalendarEvent::class.java)
            .build()
        adapter = CalendarEventAdapter(options)
        manager = LinearLayoutManager(requireActivity())
        manager.stackFromEnd = false

        binding.eventRecyclerView.layoutManager = manager
        binding.eventRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : CalendarEventAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                setFragmentResult("passEventDetails",
                    bundleOf("title" to options.snapshots[position].title,
                        "date" to options.snapshots[position].date,
                        "startTime" to options.snapshots[position].startTime,
                        "endTime" to options.snapshots[position].endTime,
                        "details" to options.snapshots[position].details))
                val action = CalendarMainFragmentDirections.actionCalendarMainFragmentToEventDetailsFragment()
                findNavController().navigate(action)
            }
        })
    }
}