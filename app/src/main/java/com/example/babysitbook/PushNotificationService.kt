package com.example.babysitbook

import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.example.babysitbook.activities.LoginActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Firebase.firestore
            .collection("DeviceTokens")
            .document()
            .set(mapOf("token" to token))
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.data["title"]
        val body = message.data["body"]

        val notification = Notification.Builder(this, "HEADS_UP_NOTIFICATIONS")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
        NotificationManagerCompat.from(this).notify(1, notification.build())
    }

}