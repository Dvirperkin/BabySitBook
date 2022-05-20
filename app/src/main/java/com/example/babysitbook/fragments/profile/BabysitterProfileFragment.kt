package com.example.babysitbook.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.babysitbook.databinding.BabysitterProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class BabysitterProfileFragment : Fragment() {
    private lateinit var binding: BabysitterProfileBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BabysitterProfileBinding.inflate(inflater)

        functions = Firebase.functions
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.EditBabyProfileBtn.setOnClickListener { editProfile(view) }

        functions.getHttpsCallable("getProfileData")
            .call(hashMapOf(
                "email" to auth.currentUser?.email,)
            )
            .continueWith{task->
                val res = task.result.data as HashMap<*, *>
                val firstLast =res["displayName"].toString().split(' ')
                val displayName= firstLast.joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }
                binding.Name.text = displayName
                binding.gender.text = res["gender"].toString()
            }
    }

     private fun editProfile(view: View) {
        val action = BabysitterProfileFragmentDirections.actionBabysitterProfileFragmentToEditBabysitterProfileFragment()
         findNavController().navigate(action)
    }
}