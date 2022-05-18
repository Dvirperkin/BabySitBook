package com.example.babysitbook.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.example.babysitbook.R
import com.example.babysitbook.databinding.FirstLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class FirstLoginFragment : Fragment(), AdapterView.OnItemSelectedListener{

    private lateinit var binding: FirstLoginBinding
    private lateinit var profileChoice: Any

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FirstLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileSpinner.onItemSelectedListener = this
//        binding.genderSpinner.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.user_kind,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.profileSpinner.adapter = adapter
        }

//        ArrayAdapter.createFromResource(
//            requireActivity(),
//            R.array.gender,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            // Specify the layout to use when the list of choices appears
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            // Apply the adapter to the spinner
////            binding.genderSpinner.adapter = adapter
//        }

//        binding.Date.setOnClickListener { showDatePickerDialog(view) }
//        setFragmentResultListener("onDateSet") { reqKey:String, bundle: Bundle ->
//            processDatePickerResult(reqKey, bundle)
//        }
        binding.submitBtn.setOnClickListener { submit(view) }

    }

    private fun submit(view: View){
        if(validateForm()){
            setFragmentResult("passProfileType",
                bundleOf("profileType" to profileChoice.toString())
            )
            if(profileChoice.toString() == "Parent"){
                view.findNavController().navigate(FirstLoginFragmentDirections.actionFirstLoginFragmentToParentEditProfileFragment())
            }
            else if(profileChoice.toString() == "Babysitter"){
                view.findNavController().navigate(FirstLoginFragmentDirections.actionFirstLoginFragmentToBabysitterEditProfileFragment())
            }
        }
//        if(auth.currentUser != null) {
//            functions.getHttpsCallable("updateNewUser").call(
//                hashMapOf(
//                    "profile" to profileChoice.toString(),
//                    "uid" to auth.currentUser!!.uid,
//                    "email" to auth.currentUser!!.email,
//                    "displayName" to binding.ETFirstName.text.toString() + " " + binding.ETLastName.text.toString(),
//                    "gender" to genderChoice.toString(),
//                    "birthdate" to binding.Date.text
//                )
//            )
//        }

//        val action = FirstLoginFragmentDirections.actionFirstLoginFragmentToHomeActivity2()
//        view.findNavController().navigate(action)
    }

    private fun validateForm() : Boolean{
        var valid = true
        if (profileChoice.toString() == "User Type"){
            (binding.profileSpinner.selectedView as TextView).error = ""
            Toast.makeText(requireActivity(), "Choose one of the options", Toast.LENGTH_LONG).show()
            valid = false
        }
//        if (genderChoice.toString() == "None"){
//            (binding.genderSpinner.selectedView as TextView).error = ""
//            Toast.makeText(requireActivity(), "Choose a gender", Toast.LENGTH_LONG).show()
//            valid = false
//        }
//        if (binding.ETFirstName.text.isEmpty()){
//            binding.ETFirstName.error = "Enter first name"
//            valid = false
//        }
//        if (binding.ETLastName.text.isEmpty()){
//            binding.ETLastName.error = "Enter last name"
//            valid = false
//        }

        return valid
    }

//    fun processDatePickerResult(reqKey:String, bundle: Bundle){
//
//        val year =  bundle["year"] as Int
//        val month = bundle["month"] as Int + 1
//        val day = bundle["day"] as Int
//
//        binding.Date.text = getString(R.string.date, day, month, year)
//    }

//    private fun showDatePickerDialog(view: View){
//        val datePickerFragment = DatePickerFragment()
//        datePickerFragment.show(parentFragmentManager, "datePicker")
//    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected.

        when(parent.id){
            binding.profileSpinner.id -> { profileChoice = parent.getItemAtPosition(pos) }
//            binding.genderSpinner.id -> { genderChoice = parent.getItemAtPosition(pos) }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
}
