package com.hasz.gymrats.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hasz.gymrats.app.R
import androidx.databinding.DataBindingUtil.setContentView
import com.hasz.gymrats.app.databinding.ActivityGymRatsRootBinding

class GymRatsRootActivity: AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView<ActivityGymRatsRootBinding>(this, R.layout.activity_gym_rats_root)
  }
}
