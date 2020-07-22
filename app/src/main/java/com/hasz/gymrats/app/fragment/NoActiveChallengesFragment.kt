package com.hasz.gymrats.app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.MainNavigationDirections
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.CreateChallengeActivity
import com.hasz.gymrats.app.activity.JoinChallengeActivity
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.databinding.FragmentNoActiveChallengesBinding
import com.hasz.gymrats.app.extension.activeOrUpcoming
import com.hasz.gymrats.app.extension.isActive
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState

class NoActiveChallengesFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    (context as? MainActivity)?.supportActionBar?.title = ""

    return DataBindingUtil.inflate<FragmentNoActiveChallengesBinding>(
      inflater, R.layout.fragment_no_active_challenges, container, false
    ).apply {
      joinButton.setOnClickListener {
        val intent = Intent().apply {
          context?.let { setClass(it, JoinChallengeActivity::class.java) }
        }

        startActivityForResult(intent, 999)
      }

      startButton.setOnClickListener {
        val intent = Intent().apply {
          context?.let { setClass(it, CreateChallengeActivity::class.java) }
        }

        startActivityForResult(intent, 999)
      }
    }.root
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (resultCode == 54321 && requestCode == 999) {
      GymRatsApi.allChallenges { result ->
        result.fold(
          onSuccess = { challenges ->
            val nav = findNavController()
            ChallengeState.allChallenges = challenges

            val activeOrUpcoming = challenges.activeOrUpcoming()
            (context as? MainActivity)?.updateNav(activeOrUpcoming)

            if (activeOrUpcoming.isEmpty()) {
              nav.navigate(MainNavigationDirections.noChallenges())
            } else {
              val challenge = activeOrUpcoming.firstOrNull { it.id == ChallengeState.lastOpenedChallengeId } ?: activeOrUpcoming.first()

              nav.popBackStack()

              if (challenge.isActive()) {
                nav.navigate(MainNavigationDirections.challengeBottomNav(challenge))
              } else {
                nav.navigate(MainNavigationDirections.upcomingChallenge(challenge))
              }
            }
          },
          onFailure = { error ->
            Snackbar.make(requireView(), error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
          }
        )
      }
    }
  }
}