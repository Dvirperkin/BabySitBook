package com.example.babysitbook.fragments.charging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.babysitbook.databinding.ChargingChooserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class ChargingChooser : Fragment(){

    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ChargingChooserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChargingChooserBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        functions = Firebase.functions
        auth = Firebase.auth

        if(auth.currentUser != null) {
            functions.getHttpsCallable("isBabysitter").call(
                hashMapOf(
                    "uid" to auth.currentUser!!.uid
                )
            ).continueWith { task ->
                val res = task.result.data as HashMap<*, *>
                if (res["isBabysitter"] as Boolean) {
                    view.findNavController().navigate(ChargingChooserDirections.actionChargingChooserToChargingFragment())
                } else {
                    view.findNavController().navigate(ChargingChooserDirections.actionChargingChooserToBillingHistory(false))
                }
            }
        }
    }
}