package com.example.babysitbook.fragments.chat

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.ChatMessagesBinding
import com.example.babysitbook.model.chat.ChatMessage
import com.example.babysitbook.model.chat.ChatMessageAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class ChatMessagesFragment : Fragment() {

    private var firestore: FirebaseFirestore = Firebase.firestore
    private lateinit var auth: FirebaseAuth


    private lateinit var query: Query
    private lateinit var binding: ChatMessagesBinding
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var chatKey: String
    private lateinit var otherEmail: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        binding = ChatMessagesBinding.inflate(inflater)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatKey = arguments?.get("chatKey").toString()
        otherEmail = arguments?.get("contactEmail").toString()

        query = firestore.collection("Chats")
            .document(chatKey)
            .collection("Messages")
            .orderBy("date")

        val options = FirestoreRecyclerOptions.Builder<ChatMessage>()
            .setQuery(query, ChatMessage::class.java)
            .build()
        adapter = ChatMessageAdapter(options)
        manager = LinearLayoutManager(context)
        manager.stackFromEnd = true

        binding.chatMessagesRecyclerView.layoutManager = manager
        binding.chatMessagesRecyclerView.adapter = adapter

        binding.sendButton.setOnClickListener {
//            val timeZone = TimeZone.getTimeZone("Asia/Jerusalem")
//            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//            val date = Calendar.getInstance(timeZone)
            val current = LocalDateTime.now(ZoneOffset.UTC)
            val ldtZone = current.atZone(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            val date = ldtZone.format(formatter)

            val chatMessage = ChatMessage(binding.messageEditText.text.toString(),
                                            date.toString(),
                                          auth.currentUser?.uid.toString())
            Firebase.functions.getHttpsCallable("sendMessage").call(
                hashMapOf(
                    "chatKey" to arguments?.get("chatKey").toString(),
                    "message" to chatMessage.message,
                    "date" to date.toString(),
                    "otherEmail" to arguments?.get("contactEmail").toString()
                )
            )
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
