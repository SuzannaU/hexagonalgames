package com.openclassrooms.hexagonal.games.data.service

import com.google.firebase.messaging.FirebaseMessagingService

class NotificationService : FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}