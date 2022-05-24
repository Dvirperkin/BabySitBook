package com.example.babysitbook.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.example.babysitbook.databinding.ParentEditProfileBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class ParentEditProfileFragment : Fragment() {

    lateinit var binding: ParentEditProfileBinding
    private lateinit var functions: FirebaseFunctions
    private lateinit var auth: FirebaseAuth
    private lateinit var profileType: Any
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= ParentEditProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        functions = Firebase.functions
        auth = Firebase.auth

        setFragmentResultListener("passProfileType") { _:String, bundle: Bundle ->
            profileType = bundle["profileType"] as String
        }

        binding.saveButton.setOnClickListener {
            saveUserDetails(view)
        }

        getUserDetails()
    }

    private fun getUserDetails(){
        if(auth.currentUser != null) {
            functions.getHttpsCallable("getProfileData").call(
                hashMapOf("email" to auth.currentUser!!.email)
            ).continueWith { task ->
                val res = task.result.data as HashMap<*, *>
                if(!(res["NoUser"] as Boolean)){
                    val name = res["displayName"].toString().split(' ')
                    binding.firstName.setText(name[0].replaceFirstChar { it.uppercase() })
                    binding.lastName.setText(name[1].replaceFirstChar { it.uppercase() })
                    binding.city.setText(res["city"].toString().replaceFirstChar { it.uppercase() })
                    binding.children.setText(res["children"] as String)
                    binding.description.setText(res["description"] as String)
                }
            }
        }
    }

    private fun saveUserDetails(view : View){
        if(validateForm()){
            functions.getHttpsCallable("isNewUser").call(hashMapOf("uid" to auth.currentUser?.uid))
                .continueWith { task ->
                    val res = task.result.data as HashMap<*, *>
                    if(res["isNewUser"] as Boolean){

                        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {  task ->
                            if (!task.isSuccessful) {
                                Log.w("TokenFail", "Fetching FCM registration token failed", task.exception)
                            }
                            token = task.result
                        })

                        updateNewUser()
                        view.findNavController().navigate(BabysitterEditProfileFragmentDirections.actionFirstLoginFragmentToHomeActivity2())
                    } else {
                        updateUser()
                        Toast.makeText(requireActivity(), "Details saved!", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun updateUser(){
        if(auth.currentUser != null) {
            functions.getHttpsCallable("updateProfileDetails").call(
                hashMapOf(
                    "uid" to auth.currentUser!!.uid,
                    "displayName" to binding.firstName.text.toString().trim().lowercase() + " "
                            + binding.lastName.text.toString().trim().lowercase(),
                    "city" to binding.city.text.toString().trim().lowercase(),
                    "children" to binding.children.text.toString().trim(),
                    "description" to binding.description.text.toString().trim()
                )
            )
        }
    }

    private fun updateNewUser(){
        if(auth.currentUser != null) {
            functions.getHttpsCallable("updateNewUser").call(
                hashMapOf(
                    "profile" to profileType.toString(),
                    "uid" to auth.currentUser!!.uid,
                    "email" to auth.currentUser!!.email,
                    "image" to "",
                    "displayName" to binding.firstName.text.toString().trim().lowercase() + " "
                            + binding.lastName.text.toString().trim().lowercase(),
                    "city" to binding.city.text.toString().trim().lowercase(),
                    "children" to binding.children.text.toString().trim(),
                    "description" to binding.description.text.toString().trim(),
                    "deviceTokens" to listOf(token)
                )
            )
        }
    }

    private fun validateForm() : Boolean{
        var valid = true

        if (binding.firstName.text.trim().isEmpty()){
            binding.firstName.error = "Enter first name"
            valid = false
        }
        if (binding.lastName.text.trim().isEmpty()){
            binding.lastName.error = "Enter last name"
            valid = false
        }

        if (binding.city.text.trim().isEmpty()){
            binding.city.error = "Choose city"
            valid = false
        }

        if (binding.children.text.trim().isEmpty()){
            binding.children.error = "Enter children number"
            valid = false
        }

        return valid
    }
}