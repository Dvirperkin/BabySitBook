package com.example.babysitbook.model.searchableProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ProfileSearchBinding
import com.example.babysitbook.fragments.searchProfile.SearchProfileResultsFragmentDirections
import com.example.babysitbook.model.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class SearchableProfileAdapter (
    private val options: FirestoreRecyclerOptions<User>
    ) : FirestoreRecyclerAdapter<User, RecyclerView.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.profile_search, parent, false)
        val binding = ProfileSearchBinding.bind(view)
        return ProfileViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: User
    ) {
        (holder as ProfileViewHolder).bind(model)
    }

    inner class ProfileViewHolder(private val binding: ProfileSearchBinding) : RecyclerView.ViewHolder(binding.root){
        private var functions = Firebase.functions

        fun bind(item : User){
            val firstLast =item.displayName.split(' ')
            val displayName= firstLast.joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }
            binding.profileTextView.text = displayName
            binding.emailTextView.text = item.email
            binding.profileTextView.setOnClickListener { view ->
                functions.getHttpsCallable("otherUserType").call(hashMapOf("email" to item.email))
                    .continueWith { task ->
                        if(task.isSuccessful){
                            val action: NavDirections
                            val res = task.result.data as HashMap<*, *>
                            action = if(res["profile"] == "Babysitter"){
                                SearchProfileResultsFragmentDirections
                                    .actionSearchProfileResultsFragmentToOtherBabysitterProfileFragment(item.email)
                            } else {
                                SearchProfileResultsFragmentDirections
                                    .actionSearchProfileResultsFragmentToOtherParentProfileFragment(item.email)
                            }
                            view.findNavController().navigate(action)

                        } else {
                            Toast.makeText(view.context, "Error: Failed to load profile", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}