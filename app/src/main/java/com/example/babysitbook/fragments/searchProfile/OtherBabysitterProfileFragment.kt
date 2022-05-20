package com.example.babysitbook.fragments.searchProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.babysitbook.R
import com.example.babysitbook.databinding.FragmentOtherBabysitterProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        checkLike()
        binding.likeBtn.setOnClickListener { likeFriend() }
    }

    private fun setDetails() {
        functions.getHttpsCallable("getProfileData").call(hashMapOf("email" to otherEmail))
            .continueWith { task ->
                if (task.isSuccessful) {
                    val res = task.result.data as HashMap<*, *>
                    if (!(res["NoUser"] as Boolean)) {
                        binding.Name.text = res["displayName"].toString().split(' ')
                            .joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }
                        binding.city.text = res["city"].toString().replaceFirstChar { it.uppercase() }
                        binding.gender.text = res["gender"] as String
                        binding.mobility.text = res["mobility"] as String
                        binding.age.text = calculateAge(res["birthdate"] as String)
                        binding.experience.text = res["experience"] as String
                        binding.hourlyRate.text = res["hourlyRate"] as String
                        binding.description.text = res["description"] as String
                        binding.likes.text = res["likes"] as String
                    } else {
                        Toast.makeText(context, "Error: Failed to load profile", Toast.LENGTH_LONG)
                            .show()
                    }
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

    private fun setRelationShip() {
        functions.getHttpsCallable("checkRelationShip").call(
            hashMapOf(
                "uid" to auth.currentUser?.uid,
                "email" to otherEmail
            )
        )
            .continueWith { task ->
                if (task.isSuccessful) {
                    val res = task.result.data as HashMap<*, *>
                    if (res["relation"] == "friends") {
                        binding.BabysitterRelationshipBtn.text = getString(R.string.delete)
                        binding.BabysitterRelationshipBtn.setOnClickListener { deleteFriend() }
                    } else if (res["relation"] == "pending") {
                        binding.BabysitterRelationshipBtn.text = getString(R.string.pending)
                        binding.BabysitterRelationshipBtn.setOnClickListener { }
                    } else {
                        binding.BabysitterRelationshipBtn.text = getString(R.string.add_friend)
                        binding.BabysitterRelationshipBtn.setOnClickListener { sendFriendRequest() }
                    }
                }
            }
    }

    private fun sendFriendRequest() {
        functions.getHttpsCallable("sendFriendRequest").call(
            hashMapOf(
                "uid" to auth.currentUser?.uid,
                "email" to otherEmail
            )
        )
            .continueWith { task ->
                if (task.isSuccessful) {
                    binding.BabysitterRelationshipBtn.text = getString(R.string.pending)
                    binding.BabysitterRelationshipBtn.setOnClickListener { }
                }
            }
    }

    private fun deleteFriend() {
        functions.getHttpsCallable("deleteFriend").call(
            hashMapOf(
                "uid" to auth.currentUser?.uid,
                "email" to otherEmail
            )
        )
            .continueWith { task ->
                if (task.isSuccessful) {
                    binding.BabysitterRelationshipBtn.text = getString(R.string.add_friend)
                    binding.BabysitterRelationshipBtn.setOnClickListener { sendFriendRequest() }
                }
            }
    }

    private fun likeFriend() {
        /**
         * <item android:drawable="@drawable/button_focused_orange" android:state_selected="true" />
         */
            functions.getHttpsCallable("likeFriend").call(
                hashMapOf(
                    "email" to otherEmail,
                )
            )
                .continueWith { task ->
                    if (task.isSuccessful) {
                        val res = task.result.data as HashMap<*, *>
                        if(res["added"] as Boolean){
                            val likes = binding.likes.text.toString().toInt() + 1
                            binding.likes.text = likes.toString()
                            binding.likeBtn.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24)
                        } else {
                            val likes = binding.likes.text.toString().toInt() - 1
                            binding.likes.text = likes.toString()
                            binding.likeBtn.setBackgroundResource(R.drawable.ic_baseline_thumb_up_off_alt_24)
                        }

                    }
                }

    }

    private fun checkLike() {
        functions.getHttpsCallable("checkLike").call(
            hashMapOf(
                "email" to otherEmail,
            )
        )
            .continueWith { task ->
                if (task.isSuccessful) {
                    val res = task.result.data as HashMap<*, *>
                    if(res["like"] as Boolean){
                        binding.likeBtn.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24)
                    } else {
                        binding.likeBtn.setBackgroundResource(R.drawable.ic_baseline_thumb_up_off_alt_24)
                    }

                }
            }
    }
}