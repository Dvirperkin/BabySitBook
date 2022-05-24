package com.example.babysitbook.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class LoginActivity() : AppCompatActivity(){

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    companion object {
        var isEmulatorsConnected: Boolean = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        functions = Firebase.functions
        firestore = Firebase.firestore
        storage = Firebase.storage

        if(!isEmulatorsConnected) {
            isEmulatorsConnected = true
            auth.useEmulator("10.0.2.2", 9099)
            functions.useEmulator("10.0.2.2", 5001)
            firestore.useEmulator("10.0.2.2", 8080)
            storage.useEmulator("10.0.2.2", 9199)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser : FirebaseUser? = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        createNotificationChannel()
    }


    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("HEADS_UP_NOTIFICATIONS", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
