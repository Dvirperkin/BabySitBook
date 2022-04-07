package com.example.babysitbook.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.babysitbook.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var binding: LoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginBinding.inflate(layoutInflater)

        auth = Firebase.auth
        email = binding.Email
        password = binding.Password
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.RegisterBtn.setOnClickListener { register(view) }
        binding.LoginBtn.setOnClickListener { login(view) }
    }


    private fun register(view: View){
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        view.findNavController().navigate(action)
    }

    fun login(view: View){
        if(email.text.isEmpty()){
            email.error = "Please enter a valid email."
            return
        }

        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireActivity(), "User logged in successfully", Toast.LENGTH_SHORT).show()
                val action = LoginFragmentDirections.actionLoginFragmentToHomeActivity()
                view.findNavController().navigate(action)
            } else {
                Toast.makeText(requireActivity(), task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}