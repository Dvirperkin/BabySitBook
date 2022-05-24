package com.example.babysitbook.model.notification

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.NotificationBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

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
            binding.notificationTitle.text = item.title

            val storageRef = Firebase.storage.reference
            val profileImageRef = storageRef.child("profileImages/" + item.email + ".jpg")
            val localFile: File = File.createTempFile("tempFile", ".jpg")

            profileImageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.contactImage.setImageBitmap(bitmap)
            }
            if(item.title == "Friend Request" || item.title == "Charge Request"
                ||item.title == "Event Sharing Request"||item.title == "Event Updating Request"){
                binding.acceptBtn.setOnClickListener { accept(item) }
                binding.ignoreBtn.setOnClickListener { ignore(item) }
            }else{
                binding.acceptBtn.visibility = View.GONE
                binding.ignoreBtn.visibility = View.GONE
            }
        }

        private fun accept(request: Notification){
            val function = Firebase.functions

            if( request.title == "Friend Request") {
                function.getHttpsCallable("acceptFriendRequest").call(
                    hashMapOf(
                        "email" to request.email
                    )
                )
            } else if ( request.title == "Charge Request" ){
                function.getHttpsCallable("acceptCharge").call(
                    hashMapOf(
                        "email" to request.email,
                        "notificationID" to request.notificationID
                    )
                )
            } else if ( request.title == "Event Sharing Request" ){
                function.getHttpsCallable("acceptEventSharing").call(
                    hashMapOf(
                        "email" to request.email,
                        "notificationID" to request.notificationID
                    )
                )
            } else if ( request.title == "Event Updating Request" ){
                function.getHttpsCallable("acceptEventUpdating").call(
                    hashMapOf(
                        "email" to request.email,
                        "notificationID" to request.notificationID
                    )
                )
            }
        }

        private fun ignore(request: Notification){
            val function = Firebase.functions

            if( request.title == "Friend Request") {
                function.getHttpsCallable("ignoreFriendRequest").call(
                    hashMapOf(
                        "email" to request.email
                    )
                )
            } else if ( request.title == "Charge Request" ){
                function.getHttpsCallable("ignoreCharge").call(
                    hashMapOf(
                        "email" to request.email,
                        "notificationID" to request.notificationID
                    )
                )
            } else if ( request.title == "Event Sharing Request" ){
                function.getHttpsCallable("ignoreEventSharing").call(
                    hashMapOf(
                        "email" to request.email,
                        "notificationID" to request.notificationID
                    )
                )
            } else if ( request.title == "Event Updating Request" ){
                function.getHttpsCallable("ignoreEventUpdating").call(
                    hashMapOf(
                        "email" to request.email,
                        "notificationID" to request.notificationID
                    )
                )
            }
        }
    }
}