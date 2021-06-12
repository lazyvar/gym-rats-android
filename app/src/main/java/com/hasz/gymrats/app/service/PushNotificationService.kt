package com.hasz.gymrats.app.service

import com.google.firebase.messaging.FirebaseMessagingService

class PushNotificationService: FirebaseMessagingService() {
  override fun onNewToken(p0: String) {
    super.onNewToken(p0)

    if (AuthService.currentAccount != null) {
      GymRatsApi.postDevice(token = p0) { _ -> }
    }
  }
}