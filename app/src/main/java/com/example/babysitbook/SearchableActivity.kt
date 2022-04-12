package com.example.babysitbook

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.model.ChatContact
import com.example.babysitbook.model.ChatContactAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchableActivity : AppCompatActivity(){
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)
        firestore = Firebase.firestore
        firestore.useEmulator("10.0.2.2",8081)
        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchProfile(query)
            }
        }
    }


    private fun searchProfile(query : String){
        //TODO
        val parentRef = firestore.collection("Parent").whereEqualTo(query,"displayName")
        val babysitterRef = firestore.collection("Babysitter").whereEqualTo(query,"displayName")

        //whereEqualTo(query,"displayName")


//        val options = FirebaseRecyclerOptions.Builder<ChatContact>()
//            .setQuery(query, ChatContact::class.java)
//            .build()
//        adapter = ChatContactAdapter(options)
//        manager = LinearLayoutManager(context)
//        manager.stackFromEnd = false
//
//        binding.chatContactsRecyclerView.layoutManager = manager
//        binding.chatContactsRecyclerView.adapter = adapter






        println(query)

    }

}