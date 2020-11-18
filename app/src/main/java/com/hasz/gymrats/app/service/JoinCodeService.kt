package com.hasz.gymrats.app.service

import android.content.Context
import android.content.SharedPreferences
import com.hasz.gymrats.app.application.GymRatsApplication

object JoinCodeService {
  private val sharedPreferences: SharedPreferences = GymRatsApplication.context!!.getSharedPreferences("com.hasz.app.gymrats", Context.MODE_PRIVATE)!!
  private const val key = "join_code"

  fun store(code: String) {
    val editor = sharedPreferences.edit()

    editor.putString(key, code)
    editor.apply()
  }

  fun retrieve(): String? {
    return sharedPreferences.getString(key, null)
  }

  fun clear() {
    val editor = sharedPreferences.edit()

    editor.remove(key)
    editor.apply()
  }
}