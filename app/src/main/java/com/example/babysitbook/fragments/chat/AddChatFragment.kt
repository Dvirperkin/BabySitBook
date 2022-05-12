package com.example.babysitbook.fragments.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.FragmentAddChatBinding
import com.example.babysitbook.model.chat.ChatContact
import com.example.babysitbook.model.chat.ShowFriendsAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddChatFragment : Fragment(){

    private var firestore: FirebaseFirestore = Firebase.firestore
    private var auth: FirebaseAuth = Firebase.auth

    private lateinit var binding: FragmentAddChatBinding
    private lateinit var query: Query
    private lateinit var adapter: ShowFriendsAdapter
    private lateinit var manager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activeChatFriends: Array<String> = arguments?.get("activeChatFriends") as Array<String>

        auth = Firebase.auth
        if(auth.currentUser != null) {

            query = if(activeChatFriends.isNotEmpty()) {
                firestore.collection("Users")
                    .document(auth.currentUser!!.uid)
                    .collection("Friends")
                    .orderBy("email")
                    .whereNotIn("email", activeChatFriends.toList())
            } else {
                firestore.collection("Users")
                    .document(auth.currentUser!!.uid)
                    .collection("Friends")
            }
                val options = FirestoreRecyclerOptions.Builder<ChatContact>()
                    .setQuery(query, ChatContact::class.java)
                    .build()

                adapter = ShowFriendsAdapter(options)
                manager = LinearLayoutManager(context)
                binding.friendsRecyclerView.layoutManager = manager
                binding.friendsRecyclerView.adapter = adapter
            }
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopListening()
    }
}



