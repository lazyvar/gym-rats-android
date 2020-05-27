package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.adapter.CompletedChallengesAdapter
import com.hasz.gymrats.app.databinding.FragmentCompletedChallengesBinding
import com.hasz.gymrats.app.extension.completed
import com.hasz.gymrats.app.service.GymRatsApi
import kotlinx.android.synthetic.main.fragment_completed_challenges.*

class CompletedChallengesFragment: Fragment() {
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewManager = LinearLayoutManager(context)
    viewAdapter = CompletedChallengesAdapter(arrayOf("Hi!"))

    return DataBindingUtil.inflate<FragmentCompletedChallengesBinding>(
      inflater, R.layout.fragment_completed_challenges, container, false
    ).apply {
      recyclerView.adapter = viewAdapter
      recyclerView.layoutManager = viewManager

      recyclerView.visibility = View.GONE
      progressBar.visibility = View.VISIBLE

      GymRatsApi.allChallenges { result ->
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        GymRatsApi.allChallenges { result ->
          result.fold(
            onSuccess = { challenges ->
              val completedChallenges = challenges.completed()

              if (completedChallenges.isEmpty()) {
                // TODO
              } else {

              }
            },
            onFailure = { error ->
              Snackbar.make(root, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
            }
          )
        }
      }
    }.root
  }
}