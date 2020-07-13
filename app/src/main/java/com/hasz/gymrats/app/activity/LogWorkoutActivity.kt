package com.hasz.gymrats.app.activity

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import com.hasz.gymrats.app.R
import kotlinx.android.synthetic.main.activity_log_workout.*

class LogWorkoutActivity: Activity() {
  private lateinit var workoutImageURI: Uri

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    workoutImageURI = intent.getParcelableExtra("workout_image_uri") as Uri
    setContentView(R.layout.activity_log_workout)

    workoutImageView.setImageURI(workoutImageURI)

    logWorkoutToolbar.setNavigationIcon(R.drawable.ic_close)
    logWorkoutToolbar.inflateMenu(R.menu.log_workout)
    logWorkoutToolbar.setNavigationOnClickListener {
      finish()
    }

    logWorkoutToolbar.setOnMenuItemClickListener { _ ->
      finish()

      return@setOnMenuItemClickListener true
    }
  }
}