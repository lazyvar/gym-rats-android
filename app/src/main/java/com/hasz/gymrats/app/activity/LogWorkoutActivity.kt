package com.hasz.gymrats.app.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.extension.active
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState
import kotlinx.android.synthetic.main.activity_log_workout.*
import kotlinx.android.synthetic.main.activity_log_workout.workoutImageView
import kotlinx.android.synthetic.main.fragment_workout.*

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

    val post = logWorkoutToolbar.menu.findItem(R.id.nav_post)
    post.isEnabled = false

    titleEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable) {}
      override fun beforeTextChanged(s: CharSequence, start: Int,
                                     count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence, start: Int,
                                 before: Int, count: Int) {
        post.isEnabled = count > 0
      }
    })

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
        challenges = ChallengeState.allChallenges.active().map { it.id }
      ) { result ->
        result.fold(
          onSuccess = { _ ->
            workoutProgressBar.visibility = View.INVISIBLE

            setResult(9114112)
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