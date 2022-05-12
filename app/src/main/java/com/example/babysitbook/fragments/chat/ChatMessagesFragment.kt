package com.example.babysitbook.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.ChatMessagesBinding
import com.example.babysitbook.model.chat.ChatMessage
import com.example.babysitbook.model.chat.ChatMessageAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase


class ChatMessagesFragment : Fragment() {

    private var firestore: FirebaseFirestore = Firebase.firestore

    private lateinit var query: Query
    private lateinit var binding: ChatMessagesBinding
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var chatKey: String

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

        chatKey = arguments?.get("chatKey").toString()

        query = firestore.collection("Chats")
            .document(chatKey)
            .collection("Messages")

        val options = FirestoreRecyclerOptions.Builder<ChatMessage>()
            .setQuery(query, ChatMessage::class.java)
            .build()
        adapter = ChatMessageAdapter(options)
        manager = LinearLayoutManager(context)
        manager.stackFromEnd = true

        binding.chatMessagesRecyclerView.layoutManager = manager
        binding.chatMessagesRecyclerView.adapter = adapter

        binding.sendButton.setOnClickListener{
            val chatMessage = ChatMessage(binding.messageEditText.text.toString())
            Firebase.functions.getHttpsCallable("sendMessage").call(hashMapOf(
                "chatKey" to arguments?.get("chatKey").toString(),
                "message" to chatMessage.message
            ))
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
