package com.hasz.gymrats.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.work.Logger
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.service.GService
import kotlinx.android.synthetic.main.activity_log_workout.*

class LogWorkoutActivity: Activity() {
  private lateinit var workoutImageURI: Uri

  @SuppressLint("RestrictedApi")
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
      GService.uploadImage(workoutImageURI) { result ->
        result.fold(
          onSuccess = { url ->
            Logger.LogcatLogger.get().warning("mack", url)
          },
          onFailure = { error ->
            Logger.LogcatLogger.get().warning("mack", error.toString())
          }
        )
      }
      return@setOnMenuItemClickListener true
    }
  }
}