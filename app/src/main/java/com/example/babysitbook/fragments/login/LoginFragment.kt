package com.example.babysitbook.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.example.babysitbook.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var functions: FirebaseFunctions
    private lateinit var binding: LoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginBinding.inflate(layoutInflater)

        functions = Firebase.functions
        auth = Firebase.auth

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

    private fun login(view: View){
        if(binding.Email.text.isEmpty()){
            binding.Email.error = "Please enter a valid email."
            return
        }

        auth.signInWithEmailAndPassword(binding.Email.text.toString(), binding.Password.text.toString()).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireActivity(), "User logged in successfully", Toast.LENGTH_SHORT).show()

                var action: NavDirections

                functions.getHttpsCallable("isNewUser").call(hashMapOf("uid" to auth.currentUser?.uid))
                    .continueWith { task ->
                        val res = task.result.data as HashMap<*, *>
                        action = if(res["isNewUser"] as Boolean){
                            LoginFragmentDirections.actionLoginFragmentToFirstLoginFragment()
                        } else {
                            LoginFragmentDirections.actionLoginFragmentToHomeActivity()
                        }

                        view.findNavController().navigate(action)
                    }
            } else {
                Toast.makeText(requireActivity(), task.exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}