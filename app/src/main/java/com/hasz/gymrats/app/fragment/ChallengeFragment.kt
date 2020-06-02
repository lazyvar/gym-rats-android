package com.hasz.gymrats.app.fragment

import android.annotation.SuppressLint
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
import com.hasz.gymrats.app.adapter.ChallengeAdapter
import com.hasz.gymrats.app.databinding.FragmentChallengeBinding
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.service.GymRatsApi

class ChallengeFragment(val challenge: Challenge): Fragment() {
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager

  @SuppressLint("RestrictedApi")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewManager = LinearLayoutManager(context)
    viewAdapter = ChallengeAdapter(challenge, arrayListOf())

    return DataBindingUtil.inflate<FragmentChallengeBinding>(
      inflater, R.layout.fragment_challenge, container, false
    ).apply {
      recyclerView.adapter = viewAdapter
      recyclerView.layoutManager = viewManager

      recyclerView.visibility = View.GONE
      progressBar.visibility = View.VISIBLE

      GymRatsApi.getAllWorkouts(challenge) { result ->
        result.fold(
          onSuccess = { workouts ->
            viewAdapter = ChallengeAdapter(challenge, workouts)
            recyclerView.adapter = viewAdapter

            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
          },
          onFailure = { error ->
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE

            Snackbar.make(root, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
          }
        )
      }
    }.root
  }
}