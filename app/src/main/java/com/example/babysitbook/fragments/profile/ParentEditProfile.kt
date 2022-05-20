package com.example.babysitbook.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.babysitbook.databinding.ParentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class ParentEditProfile : Fragment() {
    private lateinit var binding: ParentEditProfileBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ParentEditProfileBinding.inflate(inflater)
        functions = Firebase.functions
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveButton.setOnClickListener{applyEdit(view)}
    }

    private fun applyEdit(view: View){
        var displayName: String? = binding.firstName.text.toString().lowercase() + " " + binding.lastName.text.toString().lowercase()
        val filled: Array<String> = arguments?.get("attributesFiled") as Array<String>
        filled[0].split(',').map {  }

        if(auth.currentUser != null) {
            functions.getHttpsCallable("updateProfileDetails").call(
                hashMapOf(
                    "uid" to auth.currentUser?.uid,
                    "displayName" to displayName,
                    "city" to "binding.city",
                    "children" to binding.children.text.toString(),
                    "description" to binding.description.text.toString()
                )
            )
                .continueWith { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireActivity(),
                            "Data has been changed",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(requireActivity(), "Error", Toast.LENGTH_LONG).show()

                    }
                }
        }
    }

}