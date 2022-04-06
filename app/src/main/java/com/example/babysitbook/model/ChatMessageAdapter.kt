package com.example.babysitbook.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.MessageBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ChatMessageAdapter (
    private val options: FirebaseRecyclerOptions<ChatMessage>
    ) : FirebaseRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder>(options)
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
        fun bind(item : ChatMessage){
            binding.messageTextView.text = item.text
        }
    }
}