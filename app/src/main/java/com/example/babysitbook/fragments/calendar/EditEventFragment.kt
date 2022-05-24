package com.example.babysitbook.fragments.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.example.babysitbook.databinding.EditEventBinding
import com.example.babysitbook.fragments.DatePickerFragment
import com.example.babysitbook.model.TimeAsString
import com.example.babysitbook.fragments.TimePickerFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class EditEventFragment : Fragment() {
    private var startTime = false
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: EditEventBinding
    private var contactToShare: String = ""
    private var eventID: String = ""

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
        functions = Firebase.functions
        auth = Firebase.auth

        binding.editTextDate.setOnClickListener {
            showDatePickerDialog(view)
        }

        binding.editTextStartTime.setOnClickListener {
            changeStartTime(view)
        }
        binding.editTextEndTime.setOnClickListener {
            changeEndTime(view)
        }

        setFragmentResultListener("passEventDetailsToEdit") { _:String, bundle: Bundle ->
            binding.Title.setText(bundle["title"] as String)
            binding.editTextDate.text = bundle["date"] as String
            binding.editTextStartTime.text = bundle["startTime"] as String
            binding.editTextEndTime.text = bundle["endTime"] as String
            binding.details.setText(bundle["details"] as String)
            binding.chooseUser.text = bundle["contactToShare"] as String
            contactToShare = bundle["contactToShare"] as String
            eventID = bundle["eventID"] as String
            contactToShare = bundle["contactToShareEmail"] as String
        }

        setFragmentResultListener("passSelectedDate") { _:String, bundle: Bundle ->
            binding.editTextDate.text = bundle["selectedDate"] as String
        }

        setFragmentResultListener("onDateSet") { _:String, bundle: Bundle ->
            val year =  (bundle["year"] as Int).toString()
            var month = (bundle["month"] as Int + 1).toString()
            var day = (bundle["day"] as Int).toString()

            if(month.length == 1){
                month = "0$month"
            }
            if(day.length == 1){
                day = "0$day"
            }
            val date = "$day/$month/$year"
            binding.editTextDate.text = date
        }

        setFragmentResultListener("requestKey") { _, bundle ->
            val timeString = TimeAsString(bundle["hour"] as Int, bundle["minute"] as Int)
            if (startTime) {
                binding.editTextStartTime.text = timeString.getTimeString()

            } else {
                binding.editTextEndTime.text = timeString.getTimeString()
            }
        }

        if(!(arguments?.get("isUpdate") as Boolean)) {
            binding.chooseUser.setOnClickListener { setUserToShare() }
        }

        setFragmentResultListener("contactToShare") { _:String, bundle: Bundle ->
            contactToShare = bundle["shareEmail"].toString()
            binding.chooseUser.text = bundle["shareName"] as String
        }

        binding.saveEventBtn.setOnClickListener{
            saveEvent()
        }
    }

    private fun setUserToShare() {
        val action = EditEventFragmentDirections.actionEditEventFragmentToChooseFromFriendsFragment()
        findNavController().navigate(action)
    }

    private fun changeStartTime(view: View){
        startTime = true
        showTimePickerDialog(view)
    }

    private fun changeEndTime(view: View){
        startTime = false
        showTimePickerDialog(view)
    }

    private fun showTimePickerDialog(view: View) {
        TimePickerFragment().show(parentFragmentManager, "timePicker")
    }

    private fun showDatePickerDialog(view: View){
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(parentFragmentManager, "datePicker")
    }

    private fun saveEvent(){
        if(eventValidation()) {
            if (auth.currentUser != null) {
                val function = if(eventID == ""){"createEvent"} else {"updateEvent"}
                functions.getHttpsCallable(function).call(
                    hashMapOf(
                        "uid" to auth.currentUser!!.uid,
                        "title" to binding.Title.text.toString(),
                        "date" to binding.editTextDate.text.toString(),
                        "startTime" to binding.editTextStartTime.text.toString(),
                        "endTime" to binding.editTextEndTime.text.toString(),
                        "details" to binding.details.text.toString(),
                        "contactToShare" to contactToShare,
                        "eventID" to eventID
                        )
                    )
                val action = EditEventFragmentDirections.actionEditEventFragmentToCalendarMainFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun eventValidation(): Boolean {
        return if(binding.Title.text.toString() == "" ||
            binding.editTextDate.text.toString() == "" ||
            binding.editTextStartTime.text.toString() == "" ||
            binding.editTextEndTime.text.toString() == ""){
            Toast.makeText(requireActivity(), "All fields are required! (except for details)", Toast.LENGTH_LONG).show()
            false
        } else if(binding.editTextEndTime.text.toString() <= binding.editTextStartTime.text.toString()){
            Toast.makeText(requireActivity(), "Start time must be earlier than end time!",Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }
}