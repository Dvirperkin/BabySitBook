package com.example.babysitbook.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.ChatContactsBinding
import com.example.babysitbook.model.chat.ChatContact
import com.example.babysitbook.model.chat.ChatContactAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatContactFragment : Fragment(){

    private var firestore: FirebaseFirestore = Firebase.firestore
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

        binding.AddChatBtn.setOnClickListener { handleAddChat(it) }

        val auth = Firebase.auth

        if(auth.currentUser != null){
            query = firestore.collection("Users")
                .document(auth.currentUser!!.uid)
                .collection("Chats")

            val options = FirestoreRecyclerOptions.Builder<ChatContact>()
                .setQuery(query, ChatContact::class.java)
                .build()
            adapter = ChatContactAdapter(options)
            manager = LinearLayoutManager(context)
            manager.stackFromEnd = false

            binding.chatContactsRecyclerView.layoutManager = manager
            binding.chatContactsRecyclerView.adapter = adapter
        }
    }

    private fun handleAddChat(view: View) {
        if(Firebase.auth.currentUser != null)
        firestore.collection("Users")
            .document(Firebase.auth.currentUser!!.uid)
            .collection("Chats")
            .orderBy("email")
            .get()
            .addOnSuccessListener {
                var activeChatFriends: Array<String> = arrayOf()

                for (document in it.documents) {
                    activeChatFriends = activeChatFriends.plus(document.get("email").toString())
                }

                val action = ChatContactFragmentDirections
                    .actionChatContactFragmentToAddChatFragment(activeChatFriends)
                view.findNavController().navigate(action)
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
