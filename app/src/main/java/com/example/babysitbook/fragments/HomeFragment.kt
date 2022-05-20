package com.example.babysitbook.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.FragmentHomeBinding
import com.example.babysitbook.model.Post
import com.example.babysitbook.model.ShowPostAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(){
    private var firestore: FirebaseFirestore = Firebase.firestore
    private var auth: FirebaseAuth = Firebase.auth
    private var functions:FirebaseFunctions=Firebase.functions
    private val friendList = ArrayList<String>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var query: Query
    private lateinit var adapter: ShowPostAdapter
    private lateinit var manager: LinearLayoutManager
    private var displayName = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        functions.getHttpsCallable("getFriendList").call()
            .continueWith{task ->
                if(task.isSuccessful){
                    val res = task.result.data as ArrayList<String>

                    res.map { id->friendList.add(id) }
                }
            }
        functions.getHttpsCallable("getDisplayName")
            .call()
            .continueWith { task ->
                val res = task.result.data as HashMap<*, *>
                println(res["displayName"])
                val firstLast = res["displayName"].toString().split(' ')
                displayName = firstLast.joinToString(separator = " ") { word -> word.replaceFirstChar { it.uppercase() } }
            }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.postButton.setOnClickListener {handleAddPost(it)}
        auth = Firebase.auth
        if(auth.currentUser?.uid != null){
            friendList.add(auth.currentUser!!.uid)
            query = firestore.collection("Post")
                .orderBy("postedKey")
                .orderBy("date")
                .whereIn("postedKey", friendList.toList())

            val options = FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post::class.java)
                .build()
            adapter = ShowPostAdapter(options)
            manager = LinearLayoutManager(context)
            binding.feedRecyclerView.layoutManager = manager
            binding.feedRecyclerView.adapter = adapter

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleAddPost(view: View?) {
        val current = LocalDateTime.now(ZoneOffset.UTC)
        val ldtZone = current.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val date = ldtZone.format(formatter)
        val post = Post(displayName,
            auth.currentUser?.uid.toString(),
            date.toString(),
            binding.postEditText.text.toString()
            )

        Firebase.functions.getHttpsCallable("postToFeed").call(
            hashMapOf(
                "displayName" to post.displayName,
                "postedKey" to post.postedKey,
                "postContent" to post.postContent,
                "date" to date.toString()
            )
        )
        binding.postEditText.setText("")
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