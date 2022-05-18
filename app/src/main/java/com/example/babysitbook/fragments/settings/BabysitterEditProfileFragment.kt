package com.example.babysitbook.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.example.babysitbook.R
import com.example.babysitbook.databinding.BabysitterEditProfileBinding
import com.example.babysitbook.fragments.login.DatePickerFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BabysitterEditProfileFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: BabysitterEditProfileBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var profileType: Any
    private lateinit var genderChoice: Any
    private lateinit var mobilityChoice: Any

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BabysitterEditProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        functions = Firebase.functions
        auth = Firebase.auth
        functions.useEmulator("10.0.2.2", 5001)
        auth.useEmulator("10.0.2.2", 9099)

        binding.gender.onItemSelectedListener = this
        binding.mobility.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.gender,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.gender.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.mobility,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.mobility.adapter = adapter
        }

        binding.birthDate.setOnClickListener { showDatePickerDialog(view) }
        setFragmentResultListener("onDateSet") { reqKey: String, bundle: Bundle ->
            processDatePickerResult(reqKey, bundle)
            binding.birthDate.error = null;
        }

        setFragmentResultListener("passProfileType") { _:String, bundle: Bundle ->
            profileType = bundle["profileType"] as String
        }

        binding.saveButton.setOnClickListener {
            saveUserDetails(view)
        }
    }

    private fun saveUserDetails(view : View){
        if(validateForm()){
            functions.getHttpsCallable("isNewUser").call(hashMapOf("uid" to auth.currentUser?.uid))
                .continueWith { task ->
                    val res = task.result.data as HashMap<*, *>
                    if(res["isNewUser"] as Boolean){
                        updateNewUser()
                        view.findNavController().navigate(BabysitterEditProfileFragmentDirections.actionFirstLoginFragmentToHomeActivity2())
                    } else {
//                        action = LoginFragmentDirections.actionLoginFragmentToHomeActivity()
                    }
                    Toast.makeText(requireActivity(), task.result.data.toString(), Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun updateNewUser(){
        if(auth.currentUser != null) {
            functions.getHttpsCallable("updateNewUser").call(
                hashMapOf(
                    "profile" to profileType.toString(),
                    "uid" to auth.currentUser!!.uid,
                    "email" to auth.currentUser!!.email,
                    "displayName" to binding.firstName.text.toString().trim().lowercase() + " "
                                    + binding.lastName.text.toString().trim().lowercase(),
                    "gender" to genderChoice.toString(),
                    "birthdate" to binding.birthDate.text.toString(),
                    "city" to binding.city.text.toString().trim().lowercase(),
                    "experience" to binding.experience.text.trim(),
                    "hourlyRate" to binding.hourlyRate.text.trim(),
                    "mobility" to mobilityChoice.toString(),
                    "description" to binding.description.text.toString().trim()
                )
            )
        }
    }

    private fun validateForm() : Boolean{
        var valid = true
        if (genderChoice.toString() == "Gender"){
            (binding.gender.selectedView as TextView).error = "Choose a gender"
            valid = false
        }

        if (mobilityChoice.toString() == "Mobility"){
            (binding.mobility.selectedView as TextView).error = "Choose mobility"
            valid = false
        }

        if (binding.firstName.text.trim().isEmpty()){
            binding.firstName.error = "Enter first name"
            valid = false
        }
        if (binding.lastName.text.trim().isEmpty()){
            binding.lastName.error = "Enter last name"
            valid = false
        }

        if (binding.city.text.trim().isEmpty()){
            binding.city.error = "Choose city"
            valid = false
        }

        if (binding.birthDate.text.isEmpty()){
            binding.birthDate.error = "Choose birth date"
            valid = false
        } else if(binding.birthDate.text.toString() >=
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) {
            binding.birthDate.error = "Birth date should be earlier than today"
            valid = false
        }

        if (binding.experience.text.trim().isEmpty()){
            binding.experience.error = "Enter experience"
            valid = false
        }

        if (binding.hourlyRate.text.trim().isEmpty()){
            binding.hourlyRate.error = "Enter hourly rate"
            valid = false
        }

        return valid
    }

    private fun processDatePickerResult(reqKey:String, bundle: Bundle){
        val year =  bundle["year"] as Int
        val month = bundle["month"] as Int + 1
        val day = bundle["day"] as Int

        binding.birthDate.text = getString(R.string.date, day, month, year)
    }

    private fun showDatePickerDialog(view: View){
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(parentFragmentManager, "datePicker")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected.

        when (parent.id) {
            binding.gender.id -> {
                genderChoice = parent.getItemAtPosition(pos)
            }
            binding.mobility.id -> {
                mobilityChoice = parent.getItemAtPosition(pos)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}