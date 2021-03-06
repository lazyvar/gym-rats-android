package com.hasz.gymrats.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.MainNavigationDirections
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.databinding.FragmentHomeBinding
import com.hasz.gymrats.app.extension.active
import com.hasz.gymrats.app.extension.activeOrUpcoming
import com.hasz.gymrats.app.extension.isActive
import com.hasz.gymrats.app.extension.isUpcoming
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState

class HomeFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentHomeBinding>(
      inflater, R.layout.fragment_home, container, false
    ).apply {
      (context as? MainActivity)?.supportActionBar?.title = null
      (context as? MainActivity)?.supportActionBar?.setHomeButtonEnabled(false)
      (context as? MainActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)

      GymRatsApi.allChallenges { result ->
        result.fold(
          onSuccess = { challenges ->
            progressBar.visibility = View.GONE
            ChallengeState.allChallenges = challenges

            val activeOrUpcoming = challenges.activeOrUpcoming()
            val nav = findNavController()
            (requireContext() as MainActivity).updateNav(activeOrUpcoming)

            if (activeOrUpcoming.isEmpty()) {
              nav.navigate(HomeFragmentDirections.noChallenges())
            } else {
              val challenge = activeOrUpcoming.firstOrNull { it.id == ChallengeState.lastOpenedChallengeId } ?: activeOrUpcoming.first()

              if (challenge.isActive()) {
                nav.navigate(MainNavigationDirections.challengeBottomNav(challenge))
              } else {
                nav.navigate(MainNavigationDirections.upcomingChallenge(challenge))
              }
            }

            (context as? MainActivity)?.supportActionBar?.setHomeButtonEnabled(false)
            (context as? MainActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
          },
          onFailure = { error ->
            Snackbar.make(root, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
          }
        )
      }
    }.root
  }
}
