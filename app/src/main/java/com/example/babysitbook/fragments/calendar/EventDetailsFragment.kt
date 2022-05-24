package com.example.babysitbook.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.babysitbook.databinding.EventDetailsBinding
import com.example.babysitbook.fragments.charging.ChargingChooserDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class EventDetailsFragment : Fragment(){

    private lateinit var binding: EventDetailsBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var eventID: String
    private var contactToShare: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= EventDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        functions = Firebase.functions
        auth = Firebase.auth

        setFragmentResultListener("passEventDetails") { _:String, bundle: Bundle ->
            binding.EventTitleTextView.text = bundle["title"] as String
            binding.EventDateTextView.text = bundle["date"] as String
            binding.EventStartTimeTextView.text = bundle["startTime"] as String
            binding.EventEndTimeTextView.text = bundle["endTime"] as String
            binding.EventDetailsTextView.text = bundle["details"] as String
            eventID = bundle["eventID"] as String
            contactToShare = bundle["contactToShare"] as String

            functions.getHttpsCallable("getProfileData").call(
                hashMapOf(
                    "email" to bundle["contactToShare"] as String
                )
            ).continueWith { task ->
                val res = task.result.data as HashMap<*, *>
                binding.EventContactTextView.text = res["displayName"] as String
            }
        }

        binding.EditButton.setOnClickListener{
            setFragmentResult("passEventDetailsToEdit",
                bundleOf("title" to binding.EventTitleTextView.text,
                    "date" to binding.EventDateTextView.text,
                    "startTime" to binding.EventStartTimeTextView.text,
                    "endTime" to binding.EventEndTimeTextView.text,
                    "details" to binding.EventDetailsTextView.text,
                    "eventID" to eventID,
                    "contactToShare" to binding.EventContactTextView.text,
                    "contactToShareEmail" to contactToShare)
            )
            val action = EventDetailsFragmentDirections.actionEventDetailsFragmentToEditEventFragment(true)
            findNavController().navigate(action)
        }

        binding.DeleteButton.setOnClickListener {
            if (auth.currentUser != null) {
                functions.getHttpsCallable("deleteEvent").call(
                    hashMapOf(
                        "uid" to auth.currentUser!!.uid,
                        "eventID" to eventID
                    )
                )
            }
            val action = EventDetailsFragmentDirections.actionEventDetailsFragmentToCalendarMainFragment()
            findNavController().navigate(action)
        }
    }
}