package com.example.babysitbook.fragments.profile

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.babysitbook.databinding.ParentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File

class ParentProfileFragment : Fragment() {
    private lateinit var binding: ParentProfileBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private val getProfileImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            binding.profilePicture.setImageURI(uri)
            uploadImage(uri)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ParentProfileBinding.inflate(inflater)

        functions = Firebase.functions
        auth = Firebase.auth
        storage = Firebase.storage

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(auth.currentUser != null) {
            functions.getHttpsCallable("getProfileData").call(
                hashMapOf("email" to auth.currentUser!!.email)
            ).continueWith { task ->
                val res = task.result.data as HashMap<*, *>
                if (!(res["NoUser"] as Boolean)) {
                    binding.Name.text =  res["displayName"].toString().split(' ')
                        .joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }
                    binding.city.text = res["city"].toString().replaceFirstChar { it.uppercase() }
                    binding.numberOfChildren.text = res["children"] as String
                    binding.description.text = res["description"] as String
                }
            }
        }

        binding.profilePicture.setOnClickListener {
            chooseProfileImage()
        }

        val storageRef = storage.reference
        val profileImageRef = storageRef.child("profileImages/" + auth.currentUser!!.email + ".jpg")
        val localFile: File = File.createTempFile("tempFile", ".jpg")

        profileImageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.profilePicture.setImageBitmap(bitmap)
        }
    }

    private fun chooseProfileImage(){
        getProfileImage.launch("image/*")
    }

    private fun uploadImage(uri: Uri){
        val storageRef = storage.reference
        val profileImageRef = storageRef.child("profileImages/" + auth.currentUser!!.email + ".jpg")
        profileImageRef.putFile(uri)
    }
}