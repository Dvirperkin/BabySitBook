package com.example.babysitbook.fragments.searchProfile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.babysitbook.R
import com.example.babysitbook.databinding.FragmentOtherParentProfileBinding
import com.example.babysitbook.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class OtherParentProfileFragment : Fragment() {

    private lateinit var binding: FragmentOtherParentProfileBinding
    private lateinit var otherEmail: String
    private lateinit var otherUser: User
    private lateinit var myDisplayName:String

    private var functions = Firebase.functions
    private var auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentOtherParentProfileBinding.inflate(layoutInflater)
        functions.getHttpsCallable("getDisplayName").call()
            .continueWith { task ->
                val res = task.result.data as HashMap<*, *>
                println(res["displayName"])
                val firstLast = res["displayName"].toString().split(' ')
                myDisplayName = firstLast.joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }
            }
        otherEmail = arguments?.get("otherEmail").toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDetails()

        if(otherEmail != auth.currentUser?.email) {
            setRelationShip()
        } else {
            binding.ParentRelationshipBtn.visibility = View.GONE
        }

        val storageRef = Firebase.storage.reference
        val profileImageRef = storageRef.child("profileImages/$otherEmail.jpg")
        val localFile: File = File.createTempFile("tempFile", ".jpg")

        profileImageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.profilePicture.setImageBitmap(bitmap)
        }
    }

    private fun setDetails(){
        functions.getHttpsCallable("getProfileData").call(hashMapOf("email" to otherEmail))
            .continueWith {
                if(it.isSuccessful){
                    val res = it.result.data as HashMap<*,*>
                    if (!(res["NoUser"] as Boolean)) {
                        binding.Name.text =  res["displayName"].toString().split(' ')
                            .joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }
                        binding.city.text = res["city"].toString().replaceFirstChar { it.uppercase() }
                        binding.numberOfChildren.text = res["children"] as String
                        binding.description.text = res["description"] as String
                    }
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
                        binding.ParentRelationshipBtn.text = getString(R.string.delete)
                        binding.ParentRelationshipBtn.setOnClickListener { deleteFriend() }
                    } else if(res["relation"] == "pending"){
                        binding.ParentRelationshipBtn.text = getString(R.string.pending)
                        binding.ParentRelationshipBtn.setOnClickListener { }
                    } else {
                        binding.ParentRelationshipBtn.text = getString(R.string.add_friend)
                        binding.ParentRelationshipBtn.setOnClickListener { sendFriendRequest() }
                    }
                }
            }
    }

    private fun sendFriendRequest(){
        functions.getHttpsCallable("sendFriendRequest").call(hashMapOf(
            "uid" to auth.currentUser?.uid,
            "email" to otherUser.email,
            "displayName" to myDisplayName,
            "image" to otherUser.image))
            .continueWith { task ->
                if(task.isSuccessful){
                    binding.ParentRelationshipBtn.text = getString(R.string.pending)
                    binding.ParentRelationshipBtn.setOnClickListener { }
                }
            }
    }

    private fun deleteFriend(){
        functions.getHttpsCallable("deleteFriend").call(hashMapOf(
            "uid" to auth.currentUser?.uid,
            "email" to otherEmail))
            .continueWith { task ->
                if(task.isSuccessful){
                    binding.ParentRelationshipBtn.text = getString(R.string.add_friend)
                    binding.ParentRelationshipBtn.setOnClickListener { sendFriendRequest() }
                }
            }
    }
}