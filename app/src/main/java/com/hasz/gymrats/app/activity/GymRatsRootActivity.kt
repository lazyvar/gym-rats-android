package com.hasz.gymrats.app.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hasz.gymrats.app.service.AuthService

class GymRatsRootActivity: AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    var firstActivity = AuthService.retrieveAccount()?.let {
      WelcomeActivity()
    } ?: run {
      WelcomeActivity()
    }

    val intent = Intent()
    intent.setClass(applicationContext, firstActivity::class.java)

    startActivity(intent)
  }
}
