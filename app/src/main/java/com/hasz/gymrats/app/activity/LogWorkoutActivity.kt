package com.hasz.gymrats.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.work.Logger
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.service.GService
import com.hasz.gymrats.app.service.GymRatsApi
import kotlinx.android.synthetic.main.activity_log_workout.*

class LogWorkoutActivity: Activity() {
  private lateinit var workoutImageUri: Uri

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    workoutImageUri = intent.getParcelableExtra("workout_image_uri") as Uri
    setContentView(R.layout.activity_log_workout)

    workoutImageView.setImageURI(workoutImageUri)

    logWorkoutToolbar.setNavigationIcon(R.drawable.ic_close)
    logWorkoutToolbar.inflateMenu(R.menu.log_workout)
    logWorkoutToolbar.setNavigationOnClickListener {
      finish()
    }

    logWorkoutToolbar.setOnMenuItemClickListener { _ ->
      workoutProgressBar.visibility = View.VISIBLE

      GymRatsApi.postWorkout(
        uri = workoutImageUri,
        title = titleEditText.text.toString(),
        description = descriptionEditText.text.toString(),
        duration = duration.text.toString().toIntOrNull(),
        distance = distance.text.toString(),
        calories = calories.text.toString().toIntOrNull(),
        steps = steps.text.toString().toIntOrNull(),
        points = points.text.toString().toIntOrNull(),
        challenges = listOf(382)
      ) { result ->
        result.fold(
          onSuccess = { workout ->
            // TODO workout
            workoutProgressBar.visibility = View.INVISIBLE
            finish()
          },
          onFailure = { error ->
            workoutProgressBar.visibility = View.INVISIBLE
            Snackbar.make(findViewById(R.id.workoutImageView), error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
          }
        )
      }
      return@setOnMenuItemClickListener true
    }
  }
}