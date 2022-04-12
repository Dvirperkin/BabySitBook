package com.example.babysitbook.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.babysitbook.databinding.EditBabysitterFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class editBabysitterProfileFragment : Fragment() {
    private lateinit var binding: EditBabysitterFragmentBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = EditBabysitterFragmentBinding.inflate(inflater)
        functions = Firebase.functions
        auth = Firebase.auth

        functions.useEmulator("10.0.2.2",5001)
        auth.useEmulator("10.0.2.2",9099)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ApplyEditBabysitter.setOnClickListener{ applyEdit(view) }



    }
    private fun applyEdit(view: View){
        if(binding.editTextLastNameBabysitter.text.isEmpty() || binding.editTextLastNameBabysitter.text.isEmpty())
            return
        functions.getHttpsCallable("updateProfileDetails")
            .call(hashMapOf(
                "uid" to auth.currentUser?.uid,
                "type" to "Babysitter",
                "displayName" to binding.editTextNameBabysitter.text.toString() + " " + binding.editTextLastNameBabysitter.text.toString(),
            ))
            .continueWith{task->
                if(task.isSuccessful){
                    Toast.makeText(requireActivity(), "Data has been changed", Toast.LENGTH_LONG).show()
                }
            }
    }
}