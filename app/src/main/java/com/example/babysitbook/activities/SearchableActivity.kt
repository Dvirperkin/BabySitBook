package com.example.babysitbook.activities

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ActivitySearchableBinding

class SearchableActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySearchableBinding
    private lateinit var searchQuery: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchQuery = query
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val bundle = Bundle()
        bundle.putString("query", searchQuery)
        val navController = findNavController(binding.hostFragmentResults.id)
        navController.setGraph(R.navigation.nav_graph_profile_search, bundle)
    }
}