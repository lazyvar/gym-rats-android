package com.hasz.gymrats.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.model.Challenge

class ChallengePreviewActivity: AppCompatActivity() {
  lateinit var challenge: Challenge

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    challenge = intent.getParcelableExtra<Challenge>("challenge")!!

    setContentView(R.layout.activity_challenge_preview)
  }
}
