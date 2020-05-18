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
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    AuthService.retrieveAccount()?.let {

    } ?: run {

    }

    setContentView<ActivityGymRatsRootBinding>(this, R.layout.activity_gym_rats_root)
  }
}
