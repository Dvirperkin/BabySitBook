package com.example.babysitbook.model.chat

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ContactBinding
import com.example.babysitbook.fragments.chat.AddChatFragmentDirections
import com.example.babysitbook.fragments.chat.ChatContactFragmentDirections
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class ChatContactAdapter (
    private val options: FirestoreRecyclerOptions<ChatContact>
    ): FirestoreRecyclerAdapter<ChatContact, RecyclerView.ViewHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.contact, parent, false)
        val binding = ContactBinding.bind(view)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: ChatContact
    ) {
        (holder as ContactViewHolder).bind(model)
    }

    inner class ContactViewHolder(private val binding: ContactBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : ChatContact){
            binding.contactName.text = item.displayName

            val storageRef = Firebase.storage.reference
            val profileImageRef = storageRef.child("profileImages/" + item.email + ".jpg")
            val localFile: File = File.createTempFile("tempFile", ".jpg")

            profileImageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.contactImage.setImageBitmap(bitmap)
            }

            binding.contact.setOnClickListener {
                Firebase.functions.getHttpsCallable("getChatKey").call(
                    hashMapOf(
                        "email" to item.email
                    )
                ).addOnCompleteListener { task ->
                    run {
                        val res = task.result.data as HashMap<*, *>
                        openChat(it, item, res["chatKey"].toString())
                    }
                }
            }
        }

        private fun openChat(view: View, item: ChatContact, chatKey: String){

            val action = ChatContactFragmentDirections
                .actionChatContactFragmentToChatMessagesFragment(
                    item.email,
                    item.displayName,
                    item.image,
                    chatKey
                )
            view.findNavController().navigate(action)
        }
    }
}