package com.example.babysitbook.fragments.charging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ChargingFragmentBinding
import com.example.babysitbook.fragments.calendar.CalendarMainFragmentDirections
import com.example.babysitbook.fragments.calendar.EventDetailsFragmentDirections
import com.example.babysitbook.fragments.login.DatePickerFragment
import com.example.babysitbook.model.*
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class ChargingFragment : Fragment() {
    private var startTime = false
    lateinit var binding: ChargingFragmentBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: OpenBillAdapter
    private lateinit var query: Query
    private lateinit var firestore: FirebaseFirestore
    private lateinit var manager: LinearLayoutManager
    private var startTimeHour = 0
    private var startTimeMinute = 0
    private var endTimeHour = 0
    private var endTimeMinute = 0
    private var hours = 0
    private var minutes = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChargingFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = Firebase.firestore
        functions = Firebase.functions
        auth = Firebase.auth

        binding.Date.setOnClickListener {
            showDatePickerDialog(view)
        }

        setFragmentResultListener("onDateSet") { _:String, bundle: Bundle ->
            val year =  bundle["year"] as Int
            val month = bundle["month"] as Int + 1
            val day = bundle["day"] as Int

            binding.Date.text = getString(R.string.date, month, day, year)
        }

        binding.startTime.setOnClickListener {
            changeStartTime(view)
        }
        binding.endTime.setOnClickListener {
            changeEndTime(view)
        }

        setFragmentResultListener("requestKey") { _, bundle ->
            val timeString = TimeAsString(bundle["hour"] as Int, bundle["minute"] as Int)
            if (startTime) {
                startTimeHour = bundle["hour"] as Int
                startTimeMinute = bundle["minute"] as Int
                binding.startTime.text = timeString.getTimeString()

            } else {
                endTimeHour = bundle["hour"] as Int
                endTimeMinute = bundle["minute"] as Int
                binding.endTime.text = timeString.getTimeString()
            }
        }

        binding.calculateBtn.setOnClickListener{
            calculateTotalSum()
        }

        binding.chargeButton.setOnClickListener{
            charge()
            resetForm()
        }

        binding.resetButton.setOnClickListener{
            resetForm()
        }

        setRecyclerView()

        binding.historyButton.setOnClickListener{
            val action = ChargingFragmentDirections.actionChargingFragmentToBillingHistory()
            findNavController().navigate(action)
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    private fun showDatePickerDialog(view: View){
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(parentFragmentManager, "datePicker")
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

    private fun calculateTotalSum() {
        if(binding.startTime.text != "" && binding.endTime.text != "" ) {
            hours = endTimeHour - startTimeHour
            minutes =
                (endTimeMinute.toFloat() / 60.toFloat()) - (startTimeMinute.toFloat() / 60.toFloat())
            if (hours < 0) {
                hours += 24
            }
            val totalSum = ((hours + minutes) * 50).toInt().toString()
            binding.totalSum.setText(totalSum) //todo: get the money from database
        }
        else{
            createToast("All time fields are required for calculation!")
        }
    }

    private fun charge(){
        if(validation()){
            if (auth.currentUser != null) {
                functions.getHttpsCallable("charge").call(
                    hashMapOf(
                        "uid" to auth.currentUser!!.uid,
                        "date" to binding.Date.text.toString(),
                        "startTime" to binding.startTime.text.toString(),
                        "time" to hours.toString() + ":" + (minutes * 60).toInt().toString(),
                        "totalSum" to binding.totalSum.text.toString(),
                        "isOpen" to true,
                        "isPaid" to false
                        //"user" to binding.totalSum.text.toString() todo: add user to charge
                    )
                )
            }
        }
        else{
            createToast("All time fields are required!")
        }
    }

    private fun validation(): Boolean {
        return binding.Date.text != "" &&
                binding.startTime.text != "" &&
                binding.endTime.text != "" &&
                binding.totalSum.text.toString() != "" //&&
                //binding.chooseUser.text != ""
    }

    private fun resetForm(){
        binding.Date.text = ""
        binding.startTime.text = ""
        binding.endTime.text = ""
        binding.totalSum.setText("")
        binding.chooseUser.text = ""

        startTimeHour = 0
        startTimeMinute = 0
        endTimeHour = 0
        endTimeMinute = 0
        hours = 0
        minutes = 0.0f
    }

    private fun createToast(message: String){
        Toast.makeText(
            requireActivity(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun setRecyclerView() {
        query = firestore.collection("bills")
            .whereEqualTo("uid", auth.currentUser!!.uid)
            .whereEqualTo("isOpen", true).orderBy("date")

        val options = FirestoreRecyclerOptions.Builder<OpenBill>()
            .setQuery(query, OpenBill::class.java)
            .build()
        adapter = OpenBillAdapter(options)
        manager = LinearLayoutManager(context)
        manager.stackFromEnd = false

        binding.openBillingRecyclerView.layoutManager = manager
        binding.openBillingRecyclerView.adapter = adapter
    }
}