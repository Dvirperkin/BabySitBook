package com.example.babysitbook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.babysitbook.databinding.EditEventBinding
import com.example.babysitbook.model.CalendarEvent
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditEventFragment : Fragment(){
    private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    private val eventsRef = database.getReference("Calendar/Events")

    private lateinit var binding: EditEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= EditEventBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveEventBtn.setOnClickListener{
            val calendarEvent = CalendarEvent(binding.Title.text.toString(),
                binding.editTextDate.text.toString(),
                binding.editTextStartTime.text.toString(),
                binding.editTextEndTime.text.toString(),
                binding.notAvailableSwitch.isChecked,
                binding.details.text.toString())

            eventsRef.push().setValue(calendarEvent)
            binding.Title.setText("")
            binding.editTextDate.setText("")
            binding.editTextStartTime.setText("")
            binding.editTextEndTime.setText("")
            binding.notAvailableSwitch.isChecked = false
            binding.details.text = ""
        }
    }
}