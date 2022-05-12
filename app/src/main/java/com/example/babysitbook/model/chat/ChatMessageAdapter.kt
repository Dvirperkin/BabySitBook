package com.example.babysitbook.model.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.MessageBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatMessageAdapter (
    options: FirestoreRecyclerOptions<ChatMessage>
    ) : FirestoreRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder>(options)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.message, parent, false)
        val binding = MessageBinding.bind(view)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: ChatMessage
    ) {
        (holder as MessageViewHolder).bind(model)
    }

    inner class MessageViewHolder(private val binding: MessageBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("ResourceAsColor")
        fun bind(item : ChatMessage){
            binding.messageTextView.text = item.message
        }
    }
}