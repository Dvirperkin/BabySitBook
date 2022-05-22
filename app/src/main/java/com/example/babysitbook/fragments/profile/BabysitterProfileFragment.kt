package com.example.babysitbook.fragments.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.babysitbook.databinding.BabysitterProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BabysitterProfileFragment : Fragment() {
    private lateinit var binding: BabysitterProfileBinding
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
        binding = BabysitterProfileBinding.inflate(inflater)

        functions = Firebase.functions
        storage = Firebase.storage
        auth = Firebase.auth

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
                    binding.gender.text = res["gender"] as String
                    binding.mobility.text = res["mobility"] as String
                    binding.age.text = calculateAge(res["birthdate"] as String)
                    binding.experience.text = res["experience"] as String
                    binding.hourlyRate.text = res["hourlyRate"] as String
                    binding.description.text = res["description"] as String
                    binding.likes.text = res["likes"] as String
                }
            }
            binding.profilePicture.setOnClickListener {
                chooseProfileImage()
            }
        }
    }

    private fun calculateAge(birthdate : String) : String{
        val birthDate = birthdate.split("/")
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).split("/")
        var age = currentDate[2].toInt() - birthDate[2].toInt()
        if(birthDate[1].toInt() > currentDate[1].toInt() ||
            birthDate[0].toInt() > currentDate[0].toInt()){
            age -= 1
        }
        return age.toString()
    }

    private fun chooseProfileImage(){

        getProfileImage.launch("image/*")
    }

    private fun uploadImage(uri: Uri){
        val storageRef = storage.reference
        val profileImageRef = storageRef.child(auth.currentUser!!.uid + "/profileImage.jpg")
        profileImageRef.putFile(uri)
    }
}