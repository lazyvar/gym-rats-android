package com.hasz.gymrats.app.state

import android.content.Context
import android.content.SharedPreferences
import com.hasz.gymrats.app.application.GymRatsApplication
import com.hasz.gymrats.app.model.Challenge

object ChallengeState {
  private const val key = "last_opened_challenge"
  private val sharedPreferences: SharedPreferences = GymRatsApplication.context!!.getSharedPreferences("com.hasz.app.gymrats", Context.MODE_PRIVATE)!!

  var allChallenges: List<Challenge> = listOf()
  var lastOpenedChallengeId: Int
    get() { return sharedPreferences.getInt(key, 0) }
    set(value) { sharedPreferences.edit().putInt(key, value).apply() }
}