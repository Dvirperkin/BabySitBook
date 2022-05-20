package com.example.babysitbook.model

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.PostBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class ShowPostAdapter(
    options: FirestoreRecyclerOptions<Post>
) : FirestoreRecyclerAdapter<Post, RecyclerView.ViewHolder>(options){

    private lateinit var auth: FirebaseAuth
    private var functions: FirebaseFunctions = Firebase.functions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post, parent, false)
        val binding = PostBinding.bind(view)
        auth = Firebase.auth
        return PostViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: Post
    ) {
        (holder as ShowPostAdapter.PostViewHolder).bind(model)
    }
    inner class PostViewHolder(private val binding: PostBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("ResourceAsColor")
        fun bind(item : Post){
            if(item.postedKey != auth.currentUser?.uid.toString()){
                binding.deletePostButton.visibility = View.GONE;
            }
            else {
                binding.deletePostButton.setOnClickListener{ handleDelete(it,item.postID)}
            }


            binding.postUsername.text = item.displayName
            binding.postContent.text = item.postContent.trim()
            binding.postDate.text = item.date;
        }

        private fun handleDelete(it: View?,post_id: String) {
            functions.getHttpsCallable("deletePost")
                .call(hashMapOf(
                "postID" to post_id
            ))
        }
    }

}