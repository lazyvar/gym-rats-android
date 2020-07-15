package com.hasz.gymrats.app.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentWorkoutBinding
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.Workout
import com.hasz.gymrats.app.model.createdAt
import com.hasz.gymrats.app.refreshable.Refreshable
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import org.threeten.bp.format.DateTimeFormatter


class WorkoutFragment: Fragment() {
  private lateinit var workout: Workout
  private lateinit var challenge: Challenge
  private val loader = GlideLoader()
  private var savedView: View? = null

  companion object {
    fun newInstance(workout: Workout, challenge: Challenge): WorkoutFragment {
      return WorkoutFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("workout", workout);  b.putParcelable("challenge", challenge) }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (savedView != null) { return savedView }

    workout = requireArguments().getParcelable("workout")!!
    challenge = requireArguments().getParcelable("challenge")!!

    if (workout.account.id == AuthService.currentAccount!!.id) {
      setHasOptionsMenu(true)
    }

    savedView = DataBindingUtil.inflate<FragmentWorkoutBinding>(
      inflater, R.layout.fragment_workout, container, false
    ).apply {
      if (workout.photo_url != null) {
        Glide.with(requireContext())
          .load(workout.photo_url)
          .into(workoutImageView)
      }

      val desc = arrayListOf<String>()

      if (workout.description != null) {
        desc.add("${workout.description}\n")
      }

      if (workout.duration != null) {
        desc.add("Active for ${workout.duration} minutes")
      }

      if (workout.distance != null) {
        desc.add("Traveled ${workout.distance} miles")
      }

      if (workout.steps != null) {
        desc.add("Strode ${workout.steps} steps")
      }

      if (workout.calories != null) {
        desc.add("Burned ${workout.calories} calories")
      }

      if (workout.points != null) {
        desc.add("Earned ${workout.points} points")
      }

      avatarView.setOnClickListener {
        it.findNavController().navigate(WorkoutFragmentDirections.profile(profile = workout.account, challenge = challenge))
      }

      accountNameLabel.setOnClickListener {
        it.findNavController().navigate(WorkoutFragmentDirections.profile(profile = workout.account, challenge = challenge))
      }

      accountNameLabel.text = workout.account.full_name
      loader.loadImage(avatarView, workout.account.profile_picture_url ?: "", workout.account.full_name)
      titleLabel.text = workout.title
      descriptionLabel.text = desc.filterNotNull().joinToString("\n")
      timeLabel.text = workout.createdAt().format(DateTimeFormatter.ofPattern("h:mm a"))
    }.root

    return savedView
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)

    if (workout.account.id == AuthService.currentAccount!!.id) {
      inflater.inflate(R.menu.workout, menu)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.remove -> {
        AlertDialog.Builder(context)
          .setTitle("Delete workout")
          .setMessage("Are you sure you want to delete this workout?")
          .setPositiveButton(android.R.string.yes) { _, _ ->
            GymRatsApi.deleteWorkout(workout) { result ->
              result.fold(
                onSuccess = { _ ->
                  activity?.supportFragmentManager?.fragments?.forEach { frag ->
                    if (frag is Refreshable) {
                      frag.refresh()
                    }
                  }
                  activity?.findNavController(R.id.workoutImageView)?.popBackStack()
                },
                onFailure = { error ->
                  Snackbar.make(savedView!!, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
                }
              )
            }
          }
          .setCancelable(false)
          .setNeutralButton(android.R.string.no, null)
          .setIcon(android.R.drawable.ic_dialog_alert)
          .show()

        true
      }
      else -> {
        super.onOptionsItemSelected(item)
      }
    }
  }
}