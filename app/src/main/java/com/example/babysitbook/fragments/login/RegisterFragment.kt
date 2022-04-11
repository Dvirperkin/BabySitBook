package com.example.babysitbook.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.babysitbook.databinding.RegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    //FireBase API
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var binding: RegisterBinding
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegisterBinding.inflate(layoutInflater)

        auth = Firebase.auth
        firestore = Firebase.firestore

        Firebase.auth.useEmulator("10.0.2.2", 9099)
        Firebase.functions.useEmulator("10.0.2.2", 5001)
        Firebase.firestore.useEmulator("10.0.2.2", 8081)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signup.setOnClickListener{ createUser(view) }
        binding.BackBtn.setOnClickListener{ returnToLogin(view) }
    }

    private fun createUser(view: View) {
        email = binding.Email.text.toString()
        password = binding.Password.text.toString()

        if(!validateForm(email, password))
            return

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireActivity(), "Registration successful", Toast.LENGTH_LONG).show()
                        returnToLogin(view)
                    } else {
                        binding.Email.error = task.exception?.message
                    }
                }
    }

    private fun validateForm(email: String, password: String) : Boolean{
        if(binding.Email.text.isEmpty()){
            binding.Email.error = "Please enter a valid email."
            return false
        }
        if(password != binding.rePassword.text.toString()){
            binding.rePassword.error = "Confirm password doesn't match password"
            return false
        }

        return true
    }

    private fun returnToLogin(view: View){
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        view.findNavController().navigate(action)
    }

}