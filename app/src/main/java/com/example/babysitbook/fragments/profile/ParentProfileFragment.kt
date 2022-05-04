package com.example.babysitbook.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.babysitbook.databinding.ParentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class ParentProfileFragment : Fragment() {
    private lateinit var binding: ParentProfileBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ParentProfileBinding.inflate(inflater)

        functions = Firebase.functions
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.EditParentProfileBtn.setOnClickListener { editProfile(view) }

        functions.getHttpsCallable("getProfileData")
            .call(hashMapOf(
                "uid" to auth.currentUser?.email,)
            )
            .continueWith{task->
                val res = task.result.data as HashMap<*, *>

                binding.Name.text = res["displayName"].toString()
            }
    }
    private fun editProfile(view: View) {
        val action = ParentProfileFragmentDirections.actionParentProfileFragmentToEditParentProfileFragment()
        findNavController().navigate(action)
    }
}