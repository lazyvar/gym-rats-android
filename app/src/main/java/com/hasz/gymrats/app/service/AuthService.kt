package com.hasz.gymrats.app.service

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.hasz.gymrats.app.application.GymRatsApplication
import com.hasz.gymrats.app.model.Account

object AuthService {
  private const val key = "current_account"
  private val sharedPreferences: SharedPreferences = GymRatsApplication.context!!.getSharedPreferences("com.hasz.app.gymrats", Context.MODE_PRIVATE)!!

  fun retrieveAccount(): Account? {
    val json = sharedPreferences.getString(key, null)

    return json?.let {
      Gson().fromJson(it, Account::class.java)
    }
  }

  fun storeAccount(account: Account) {
    val json = Gson().toJson(account)
    val editor = sharedPreferences.edit()

    editor.putString(key, json)
    editor.apply()
  }
}