package com.example.babysitbook.fragments.searchProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.babysitbook.R
import com.example.babysitbook.databinding.FragmentOtherBabysitterProfileBinding
import com.example.babysitbook.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class OtherBabysitterProfileFragment : Fragment() {

    private lateinit var binding: FragmentOtherBabysitterProfileBinding
    private lateinit var otherEmail: String

    private var functions = Firebase.functions
    private var auth = Firebase.auth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentOtherBabysitterProfileBinding.inflate(layoutInflater)
        otherEmail = arguments?.get("otherEmail").toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDetails()
        setRelationShip()

    }

    private fun setDetails(){
        functions.getHttpsCallable("getProfileData").call(hashMapOf("email" to otherEmail))
            .continueWith { task ->
                if(task.isSuccessful){
                    val res = task.result.data as HashMap<*,*>
                    val firstLast = res["displayName"].toString().split(' ')
                    val displayName = firstLast.joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }

                    binding.Name.text = displayName
                    binding.Gender.text = res["Gender"].toString()
                } else {
                    Toast.makeText(context, "Error: Failed to load profile", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setRelationShip(){
        functions.getHttpsCallable("checkRelationShip").call(hashMapOf(
            "uid" to auth.currentUser?.uid,
            "email" to otherEmail))
            .continueWith { task ->
                if(task.isSuccessful){
                    val res = task.result.data as HashMap<*,*>
                    if(res["relation"] == "friends"){
                        binding.BabysitterRelationshipBtn.text = getString(R.string.delete)
                        binding.BabysitterRelationshipBtn.setOnClickListener { deleteFriend() }
                    } else if(res["relation"] == "pending"){
                        binding.BabysitterRelationshipBtn.text = getString(R.string.pending)
                        binding.BabysitterRelationshipBtn.setOnClickListener { }
                    } else {
                        binding.BabysitterRelationshipBtn.text = getString(R.string.add_friend)
                        binding.BabysitterRelationshipBtn.setOnClickListener { sendFriendRequest() }
                    }
                }
            }
    }

    private fun sendFriendRequest(){
        functions.getHttpsCallable("sendFriendRequest").call(hashMapOf(
            "uid" to auth.currentUser?.uid,
            "email" to otherEmail))
            .continueWith { task ->
                if(task.isSuccessful){
                    binding.BabysitterRelationshipBtn.text = getString(R.string.pending)
                    binding.BabysitterRelationshipBtn.setOnClickListener { }
                }
            }
    }

    private fun deleteFriend(){
        functions.getHttpsCallable("deleteFriend").call(hashMapOf(
            "uid" to auth.currentUser?.uid,
            "email" to otherEmail))
            .continueWith { task ->
                if(task.isSuccessful){
                    binding.BabysitterRelationshipBtn.text = getString(R.string.add_friend)
                    binding.BabysitterRelationshipBtn.setOnClickListener { sendFriendRequest() }
                }
            }
    }
}