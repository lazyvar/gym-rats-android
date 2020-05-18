package com.hasz.gymrats.app.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hasz.gymrats.app.R
import androidx.databinding.DataBindingUtil.setContentView
import androidx.work.Logger
import com.hasz.gymrats.app.databinding.ActivityGymRatsRootBinding
import com.hasz.gymrats.app.service.AuthService

class GymRatsRootActivity: AppCompatActivity() {
  @SuppressLint("RestrictedApi")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    AuthService.retreiveAccount()?.let {
      Logger.LogcatLogger.get().info("Mack", it.toString())
    } ?: run {
      Logger.LogcatLogger.get().info("Mack", "Nope.")
    }

    setContentView<ActivityGymRatsRootBinding>(this, R.layout.activity_gym_rats_root)
  }
}
