package com.example.babysitbook.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.babysitbook.databinding.FragmentProfileChooserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase


class ProfileChooserFragment: Fragment() {
    private lateinit var functions: FirebaseFunctions
    private lateinit var binding : FragmentProfileChooserBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileChooserBinding.inflate(inflater)

        functions = Firebase.functions
        auth = Firebase.auth

        chooseProfile()

        return binding.root
    }
    private fun chooseProfile(){
        var action : NavDirections
        functions.getHttpsCallable("isBabysitter").call(hashMapOf("uid" to auth.currentUser?.uid))
            .continueWith{ task ->
                val res = task.result.data as HashMap<*,*>
                action = if(res["isBabysitter"] as Boolean)
                    ProfileChooserFragmentDirections.actionProfileChooserFragmentToBabysitterProfileFragment()
                else
                    ProfileChooserFragmentDirections.actionProfileChooserFragmentToParentProfileFragment()
                findNavController().navigate(action)
            }
    }
}

