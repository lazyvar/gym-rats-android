package com.hasz.gymrats.app.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.GymRatsRootActivity
import com.hasz.gymrats.app.adapter.WorkoutAdapter
import com.hasz.gymrats.app.application.GymRatsApplication
import com.hasz.gymrats.app.databinding.FragmentChallengeBinding
import com.hasz.gymrats.app.databinding.FragmentWorkoutBinding
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.Comment
import com.hasz.gymrats.app.model.Workout
import com.hasz.gymrats.app.refreshable.Refreshable
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import kotlinx.android.synthetic.main.activity_log_workout.*
import kotlinx.android.synthetic.main.fragment_workout.*
import org.threeten.bp.Instant

class WorkoutFragment: Fragment() {
  private lateinit var workout: Workout
  private lateinit var challenge: Challenge
  private var savedView: View? = null
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager
  private lateinit var binding: FragmentWorkoutBinding

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

    viewAdapter = WorkoutAdapter(this, workout, challenge, listOf())
    viewManager = LinearLayoutManager(context)

    savedView = DataBindingUtil.inflate<FragmentWorkoutBinding>(
      inflater, R.layout.fragment_workout, container, false
    ).apply {
      binding = this
      recyclerView.adapter = viewAdapter
      recyclerView.layoutManager = viewManager

      swipeRefresh.setOnRefreshListener {
        refresh()
      }

      refresh()
    }.root

    return savedView
  }

  fun postComment(content: String) {
    binding.recyclerView.visibility = View.GONE
    binding.progressBar.visibility = View.VISIBLE

    GymRatsApi.postComment(content, workout) { result ->
      result.fold(
        onSuccess = { _ ->
          refresh()
        },
        onFailure = { error ->
          binding.recyclerView.visibility = View.VISIBLE
          binding.progressBar.visibility = View.GONE

          Snackbar.make(this.requireView(), error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
        }
      )
    }
  }

  private fun refresh() {
    binding.apply {
      GymRatsApi.getComments(workout) { result ->
        result.fold(
          onSuccess = { comments ->
            viewAdapter = WorkoutAdapter(
              this@WorkoutFragment,
              workout,
              challenge,
              comments
            )

            recyclerView.adapter = viewAdapter
            swipeRefresh.isRefreshing = false
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
          },
          onFailure = { _ ->
            swipeRefresh.isRefreshing = false
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
          }
        )
      }
    }
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