package com.example.babysitbook.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.ChatMessagesBinding
import com.example.babysitbook.model.chat.ChatMessage
import com.example.babysitbook.model.chat.ChatMessageAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatMessagesFragment : Fragment() {
    private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    private val messagesRef = database.getReference("Chat/Messages")

    private lateinit var binding: ChatMessagesBinding
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var manager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatMessagesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView8.setOnClickListener {
            val action = ChatMessagesFragmentDirections.actionChatMessagesFragmentToChatContactFragment()
            findNavController().navigate(action)
        }


        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(messagesRef, ChatMessage::class.java)
            .build()
        adapter = ChatMessageAdapter(options)
        manager = LinearLayoutManager(context)
        manager.stackFromEnd = true

        binding.chatMessagesRecyclerView.layoutManager = manager
        binding.chatMessagesRecyclerView.adapter = adapter


        binding.sendButton.setOnClickListener{
            val chatMessage = ChatMessage(binding.messageEditText.text.toString())
            messagesRef.push().setValue(chatMessage)
            binding.messageEditText.setText("")
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
}
