package com.example.babysitbook.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.babysitbook.databinding.EditParentFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class EditParentProfileFragment : Fragment() {
    private lateinit var binding: EditParentFragmentBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditParentFragmentBinding.inflate(inflater)
        functions = Firebase.functions
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ApplyEditParent.setOnClickListener{applyEdit(view)}
    }

    private fun applyEdit(view: View){
        if(binding.editTextLastNameParent.text.isEmpty() || binding.editTextLastNameParent.text.isEmpty())
            return
        functions.getHttpsCallable("updateProfileDetails")
            .call(hashMapOf(
                "uid" to auth.currentUser?.uid,
                "type" to "Parent",
                "displayName" to binding.editTextNameParent.text.toString() + " " + binding.editTextLastNameParent.text.toString(),
            ))
            .continueWith{task->
                if(task.isSuccessful){
                    Toast.makeText(requireActivity(), "Data has been changed", Toast.LENGTH_LONG).show()
                }
            }
    }

}