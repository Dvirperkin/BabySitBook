package com.example.babysitbook.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.example.babysitbook.databinding.ParentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class ParentEditProfileFragment : Fragment() {

    lateinit var binding: ParentEditProfileBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var profileType: Any

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ParentEditProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        functions = Firebase.functions
        auth = Firebase.auth
        functions.useEmulator("10.0.2.2", 5001)
        auth.useEmulator("10.0.2.2", 9099)


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
                    "city" to binding.city.text.toString().trim().lowercase(),
                    "children" to binding.children.text.trim(),
                    "description" to binding.description.text.toString().trim()
                )
            )
        }
    }

    private fun validateForm() : Boolean{
        var valid = true

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

        if (binding.children.text.trim().isEmpty()){
            binding.children.error = "Enter children number"
            valid = false
        }

        return valid
    }
}