package com.example.babysitbook.model.calendar

import com.example.babysitbook.model.User
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ContactBinding
import com.example.babysitbook.fragments.calendar.ChooseFromFriendsFragment
import com.example.babysitbook.fragments.calendar.ChooseFromFriendsFragmentDirections
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class FriendsListAdapter (
    private val fragment: ChooseFromFriendsFragment,
    private val options: FirestoreRecyclerOptions<User>
): FirestoreRecyclerAdapter<User, RecyclerView.ViewHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact, parent, false)
        val binding = ContactBinding.bind(view)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: User
    ) {
        (holder as FriendViewHolder).bind(model)
    }

    inner class FriendViewHolder(private val binding: ContactBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : User){
            binding.contactName.text = item.displayName.split(' ')
                .joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }

            binding.contact.setOnClickListener { view ->
                fragment.setFragmentResult("contactToShare", bundleOf(
                    "shareEmail" to item.email,
                    "shareName" to item.displayName
                ))

                val action = ChooseFromFriendsFragmentDirections.actionChooseFromFriendsFragmentToEditEventFragment(false)
                fragment.findNavController().navigate(action)
            }
        }
    }
}