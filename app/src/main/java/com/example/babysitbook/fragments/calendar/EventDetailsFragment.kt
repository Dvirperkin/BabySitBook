package com.example.babysitbook.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.EditEventBinding
import com.example.babysitbook.databinding.EventDetailsBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EventDetailsFragment : Fragment(){

    private lateinit var binding: EventDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= EventDetailsBinding.inflate(inflater)
        return binding.root
    }
}