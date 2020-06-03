package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentWorkoutBinding
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Workout
import kotlinx.android.synthetic.main.item_workout.*
import org.threeten.bp.format.DateTimeFormatter

class WorkoutFragment: Fragment() {
  private lateinit var workout: Workout
  private val loader = GlideLoader()

  companion object {
    fun newInstance(workout: Workout): WorkoutFragment {
      return WorkoutFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("workout", workout) }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    workout = requireArguments().getParcelable("workout")!!

    return DataBindingUtil.inflate<FragmentWorkoutBinding>(
      inflater, R.layout.fragment_workout, container, false
    ).apply {
      if (workout.photo_url != null) {
        Glide.with(requireContext())
          .load(workout.photo_url)
          .into(workoutImageView)
      }

      loader.loadImage(avatarView, workout.account.profile_picture_url ?: "", workout.account.full_name)
      titleLabel.text = workout.title
      descriptionLabel.text = workout.description
      timeLabel.text = workout.created_at.format(DateTimeFormatter.ofPattern("h:mm a"))
    }.root
  }
}