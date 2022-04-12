package com.example.babysitbook.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.babysitbook.databinding.EditEventBinding
import com.example.babysitbook.fragments.calendar.EditEventFragmentDirections
import com.example.babysitbook.model.CalendarEvent
import com.example.babysitbook.model.TimePickerFragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditEventFragment : Fragment() {
    private val database =
        Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    private val eventsRef = database.getReference("Calendar/Events")
    private var startTime = false

    private lateinit var binding: EditEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditEventBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextStartTime.setOnClickListener {
            changeStartTime(view)
        }
        binding.editTextEndTime.setOnClickListener {
            changeEndTime(view)
        }

        setFragmentResultListener("requestKey") { _, bundle ->
            val hour = bundle["hour"]
            val minute = bundle["minute"]
            val time = "$hour : $minute"
            if (startTime) {
                binding.editTextStartTime.text = time

            } else {
                binding.editTextEndTime.text = time
            }
        }

        setFragmentResultListener("requestDateKey") { _, bundle ->
            //TODO
        }

        binding.saveEventBtn.setOnClickListener{
            val calendarEvent = CalendarEvent(binding.Title.text.toString(),
                binding.editTextDate.text.toString(),
                binding.editTextStartTime.text.toString(),
                binding.editTextEndTime.text.toString(),
                binding.details.text.toString())

            eventsRef.push().setValue(calendarEvent)

            binding.Title.setText("")
            binding.editTextDate.setText("")
            binding.editTextStartTime.setText("")
            binding.editTextEndTime.setText("")
            binding.details.text = ""

            val action = EditEventFragmentDirections.actionEditEventFragmentToCalendarMainFragment()
            findNavController().navigate(action)
        }
    }

    fun changeStartTime(view: View){
        startTime = true
        showTimePickerDialog(view)
    }

    fun changeEndTime(view: View){
        startTime = false
        showTimePickerDialog(view)
    }

    private fun showTimePickerDialog(view: View) {
        TimePickerFragment().show(parentFragmentManager, "timePicker")
    }
}