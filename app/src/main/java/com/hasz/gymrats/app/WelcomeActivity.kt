package com.hasz.gymrats.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class WelcomeActivity: AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_welcome)

    val getStartedButton = findViewById<Button>(R.id.get_started)
    val loginButton = findViewById<Button>(R.id.log_in)

    getStartedButton.setOnClickListener {
      println("Get started")
    }

    loginButton.setOnClickListener {
      println("Log in")
    }
  }
}
