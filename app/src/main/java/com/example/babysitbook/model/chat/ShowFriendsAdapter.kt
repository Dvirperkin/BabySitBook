package com.example.babysitbook.model.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ContactBinding
import com.example.babysitbook.fragments.chat.AddChatFragmentDirections
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class ShowFriendsAdapter (
    private val options: FirestoreRecyclerOptions<ChatContact>
): FirestoreRecyclerAdapter<ChatContact, RecyclerView.ViewHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact, parent, false)
        val binding = ContactBinding.bind(view)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: ChatContact
    ) {
        (holder as FriendViewHolder).bind(model)
    }

    inner class FriendViewHolder(private val binding: ContactBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : ChatContact){
            binding.contactName.text = item.displayName

            binding.contact.setOnClickListener { view ->
                run {
                    Firebase.functions.getHttpsCallable("createNewChat").call(
                        hashMapOf(
                            "email" to item.email
                        )
                    ).addOnCompleteListener {

                        val res = it.result.data as HashMap<*,*>
                        openChat(view, item, res["chatKey"].toString())
                    }
                }
            }
        }

        private fun openChat(view: View, item: ChatContact, chatKey: String){

            val action = AddChatFragmentDirections
                .actionAddChatFragmentToChatMessagesFragment(
                    item.email,
                    item.displayName,
                    item.image,
                    chatKey
                )
            view.findNavController().navigate(action)
        }
    }
}