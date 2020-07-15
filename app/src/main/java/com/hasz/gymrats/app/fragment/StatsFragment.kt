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
import com.hasz.gymrats.app.adapter.StatsAdapter
import com.hasz.gymrats.app.databinding.FragmentCompletedChallengesBinding
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.StatsRow
import com.hasz.gymrats.app.service.GymRatsApi

class StatsFragment: Fragment() {
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager
  private lateinit var challenge: Challenge

  private var savedView: View? = null

  companion object {
    fun newInstance(challenge: Challenge): StatsFragment {
      return StatsFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("challenge", challenge) }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    viewManager = LinearLayoutManager(context)
    challenge = requireArguments().getParcelable("challenge")!!
    viewAdapter = StatsAdapter(challenge, listOf())

    savedView = DataBindingUtil.inflate<FragmentCompletedChallengesBinding>(
      inflater, R.layout.fragment_completed_challenges, container, false
    ).apply {
      recyclerView.adapter = viewAdapter
      recyclerView.layoutManager = viewManager

      recyclerView.visibility = View.GONE
      progressBar.visibility = View.VISIBLE

      GymRatsApi.rankings(challenge) { result ->
        recyclerView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        result.fold(
          onSuccess = { rankings ->
            viewAdapter = StatsAdapter(challenge, listOf(StatsRow(challenge = challenge)) + rankings.map { StatsRow(ranking = it) })
            recyclerView.adapter = viewAdapter
          },
          onFailure = { error ->
            Snackbar.make(root, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
          }
        )
      }
    }.root

    return savedView
  }
}