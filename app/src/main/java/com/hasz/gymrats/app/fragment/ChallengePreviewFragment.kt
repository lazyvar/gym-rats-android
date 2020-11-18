package com.hasz.gymrats.app.fragment

import android.app.Activity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.ChallengePreviewActivity
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.databinding.FragmentChallengePreviewBinding
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState
import org.threeten.bp.format.DateTimeFormatter

class ChallengePreviewFragment: Fragment() {
  lateinit var challenge: Challenge

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return DataBindingUtil.inflate<FragmentChallengePreviewBinding>(
      inflater, R.layout.fragment_challenge_preview, container, false
    ).apply {
      challenge = (context as? ChallengePreviewActivity)!!.challenge

      startDate.text = challenge.start_date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
      endDate.text = challenge.end_date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
      description.text = challenge.description
      scoredBy.text = challenge.score_by?.capitalize()
      description.movementMethod = LinkMovementMethod.getInstance()

      if (challenge.description.isNullOrEmpty()) {
        description.visibility = View.GONE
      }

      if (challenge.profile_picture_url == null) {
        imageView4.visibility = View.GONE
      } else {
        Glide.with(imageView4.context)
          .load(challenge.profile_picture_url)
          .centerCrop()
          .into(imageView4)
      }

      toolbar.setNavigationOnClickListener {
        (context as Activity).finish()
      }

      challengePreviewHeader.text = challenge.name

      letsDoThisButton.setOnClickListener {
        progressBar.visibility = View.VISIBLE
        letsDoThisButton.isEnabled = false

        GymRatsApi.joinChallenge(code = challenge.code) { result ->
          progressBar.visibility = View.INVISIBLE
          letsDoThisButton.isEnabled = true

          result.fold(
            onSuccess = { challenge ->
              ChallengeState.lastOpenedChallengeId = challenge.id
              activity?.setResult(54321)
              activity?.finish()
              MainActivity.shared?.refresh()
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