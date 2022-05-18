package com.example.babysitbook.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.babysitbook.databinding.EditEventBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class EditProfileFragment : Fragment(){

    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: EditEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditEventBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        functions = Firebase.functions
        auth = Firebase.auth
        functions.useEmulator("10.0.2.2", 5001)
        auth.useEmulator("10.0.2.2", 9099)

        if(auth.currentUser != null) {
            functions.getHttpsCallable("isBabysitter").call(
                hashMapOf(
                    "uid" to auth.currentUser!!.uid
                )
            ).continueWith { task ->
                val res = task.result.data as HashMap<*, *>
                if (res["isBabysitter"] as Boolean) {
                    view.findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToBabysitterEditProfileFragment())
                } else {
                    view.findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToParentEditProfileFragment())
                }
            }
        }
    }
}