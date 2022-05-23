package com.example.babysitbook.fragments.searchProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.FragmentSearchProfileResultsBinding
import com.example.babysitbook.model.searchableProfile.SearchableProfileAdapter
import com.example.babysitbook.model.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SearchProfileResultsFragment : Fragment() {

    private lateinit var binding: FragmentSearchProfileResultsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var searchQuery: String


    private lateinit var adapter: SearchableProfileAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var query: Query

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSearchProfileResultsBinding.inflate(layoutInflater)

        // gets the query from the activity

        searchQuery = arguments?.get("query").toString()

        firestore = Firebase.firestore
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchProfile(searchQuery)
    }

    private fun searchProfile(searchQuery : String){
        query = firestore.collection("Users")
            .orderBy("displayName")
            .startAt(searchQuery.lowercase())
            .endAt(searchQuery.lowercase() + '\uf8ff')


        firestore.collection("Users")
        val options = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()
        adapter = SearchableProfileAdapter(options)
        manager = LinearLayoutManager(requireActivity())

        binding.Results.layoutManager = manager
        binding.Results.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onPause() {
        super.onPause()
    }
}