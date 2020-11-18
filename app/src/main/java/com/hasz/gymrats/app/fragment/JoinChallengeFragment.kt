package com.hasz.gymrats.app.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentJoinChallengeBinding
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState

class JoinChallengeFragment: Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentJoinChallengeBinding>(
      inflater, R.layout.fragment_join_challenge, container, false
    ).apply {
      toolbar.setNavigationOnClickListener {
        (context as Activity).finish()
      }

      joinButton.setOnClickListener {
        val code = code.editText?.text?.toString() ?: ""

        if (code.isEmpty()) {
          return@setOnClickListener
        }

        joinButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        GymRatsApi.joinChallenge(code) { result ->
          joinButton.isEnabled = true
          progressBar.visibility = View.INVISIBLE

          result.fold(
            onSuccess = { challenge ->
              ChallengeState.lastOpenedChallengeId = challenge.id
              (context as? Activity)?.setResult(54321)
              (context as? Activity)?.finish()
            },
            onFailure = { error ->
              Snackbar.make(
                it,
                error.message ?: "Something unpredictable happened.",
                Snackbar.LENGTH_LONG
              ).show()
            }
          )
        }
      }
    }.root
  }
}