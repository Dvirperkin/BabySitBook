package com.example.babysitbook.model.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.NotificationBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class NotificationAdapter(
    val options: FirestoreRecyclerOptions<Notification>
) : FirestoreRecyclerAdapter<Notification, RecyclerView.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.notification, parent, false)
        val binding = NotificationBinding.bind(view)
        return FriendRequestViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: Notification
    ) {
        (holder as FriendRequestViewHolder).bind(model)
    }

    inner class FriendRequestViewHolder(val binding: NotificationBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: Notification){
            binding.notificationDescription.text = item.text


            binding.acceptBtn.setOnClickListener { accept(item) }
            binding.ignoreBtn.setOnClickListener { ignore(item) }
        }

        private fun accept(request: Notification){
            val function = Firebase.functions
            function.getHttpsCallable("acceptFriendRequest").call(hashMapOf(
                "email" to request.email
            ))
        }

        private fun ignore(request: Notification){
            val function = Firebase.functions
            function.getHttpsCallable("ignoreFriendRequest").call(hashMapOf(
                "email" to request.email
            ))
        }
    }
}