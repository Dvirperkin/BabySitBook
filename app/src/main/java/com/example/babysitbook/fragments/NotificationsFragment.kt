package com.example.babysitbook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.FragmentNotificationsBinding
import com.example.babysitbook.model.notification.Notification
import com.example.babysitbook.model.notification.NotificationAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class NotificationsFragment : Fragment(){
    private lateinit var binding: FragmentNotificationsBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var functions: FirebaseFunctions

    private lateinit var adapter: NotificationAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var query: Query

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        firestore = Firebase.firestore
        functions = Firebase.functions

        binding = FragmentNotificationsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        query = firestore.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("Notifications")
            .orderBy("date",Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Notification>()
            .setQuery(query, Notification::class.java)
            .build()

        adapter = NotificationAdapter(options)
        manager = LinearLayoutManager(context)

        binding.notificationsRecyclerView.adapter = adapter
        binding.notificationsRecyclerView.layoutManager = manager
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