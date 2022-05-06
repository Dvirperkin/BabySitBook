package com.example.babysitbook.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.ChatContactsBinding
import com.example.babysitbook.model.chat.ChatContact
import com.example.babysitbook.model.chat.ChatContactAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatContactFragment : Fragment(){
    private val database = Firebase.database("https://babysitbook-4e036-default-rtdb.europe-west1.firebasedatabase.app")
    private val contactRef = database.getReference("Chat/Messages")

    private lateinit var query: Query
    private lateinit var binding: ChatContactsBinding
    private lateinit var adapter: ChatContactAdapter
    private lateinit var manager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatContactsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.textView7.setOnClickListener {
//            val action = ChatContactFragmentDirections.actionChatContactFragmentToChatMessagesFragment()
//            findNavController().navigate(action)
//        }

        val auth = FirebaseAuth.getInstance().currentUser

        if(auth != null){
            query = contactRef.startAt(auth.uid).endAt(auth.uid)

            val options = FirebaseRecyclerOptions.Builder<ChatContact>()
                .setQuery(query, ChatContact::class.java)
                .build()
            adapter = ChatContactAdapter(options)
            manager = LinearLayoutManager(context)
            manager.stackFromEnd = false

            binding.chatContactsRecyclerView.layoutManager = manager
            binding.chatContactsRecyclerView.adapter = adapter
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
