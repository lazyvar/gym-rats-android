package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.adapter.ChallengeAdapter
import com.hasz.gymrats.app.databinding.FragmentChallengeBinding
import com.hasz.gymrats.app.extension.completed
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.refreshable.Refreshable
import com.hasz.gymrats.app.service.GymRatsApi

class ChallengeFragment: Fragment(), Refreshable {
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var viewManager: RecyclerView.LayoutManager
  private lateinit var challenge: Challenge
  private lateinit var binding: FragmentChallengeBinding

  private var savedView: View? = null

  companion object {
    fun newInstance(challenge: Challenge): ChallengeFragment {
      return ChallengeFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("challenge", challenge) }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    challenge = requireArguments().getParcelable("challenge")!!
    (context as? MainActivity)?.supportActionBar?.title = challenge.name

    if (savedView != null) {
      return savedView
    }

    viewManager = LinearLayoutManager(context)
    viewAdapter = ChallengeAdapter(challenge, arrayListOf())

    savedView = DataBindingUtil.inflate<FragmentChallengeBinding>(
      inflater, R.layout.fragment_challenge, container, false
    ).apply {
      binding = this

      if (challenge.completed()) {
        val p = recyclerView.layoutParams as ViewGroup.MarginLayoutParams
        p.bottomMargin = 0

        recyclerView.layoutParams = p
        recyclerView.requestLayout()

        setHasOptionsMenu(true)
      }

      refresh()
    }.root

    return savedView
  }

  override fun refresh() {
    binding.apply {
      recyclerView.adapter = viewAdapter
      recyclerView.layoutManager = viewManager

      recyclerView.visibility = View.GONE
      progressBar.visibility = View.VISIBLE

      GymRatsApi.getAllWorkouts(challenge) { result ->
        result.fold(
          onSuccess = { workouts ->
            GymRatsApi.getChallengeInfo(challenge) { result ->
              result.fold(
                onSuccess = { challengeInfo ->
                  viewAdapter = ChallengeAdapter(challenge, workouts, challengeInfo)
                  recyclerView.adapter = viewAdapter

                  recyclerView.visibility = View.VISIBLE
                  progressBar.visibility = View.GONE
                },
                onFailure = { error ->
                  recyclerView.visibility = View.VISIBLE
                  progressBar.visibility = View.GONE

                  Snackbar.make(
                    root,
                    error.message ?: "Something unpredictable happened.",
                    Snackbar.LENGTH_LONG
                  ).show()
                })
            }
          },
          onFailure = { error ->
            recyclerView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE

            Snackbar.make(
              root,
              error.message ?: "Something unpredictable happened.",
              Snackbar.LENGTH_LONG
            ).show()
          }
        )
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)

    inflater.inflate(R.menu.completed_challenge, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.nav_chat -> {
        findNavController().navigate(
          ChallengeFragmentDirections.chat(challenge)
        )

        true
      }
      R.id.nav_stats -> {
        findNavController().navigate(
          ChallengeFragmentDirections.stats(challenge)
        )

        true
      }
      else -> { super.onOptionsItemSelected(item) }
    }
  }
}