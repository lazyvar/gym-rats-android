package com.hasz.gymrats.app.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
import com.hasz.gymrats.app.databinding.FragmentCreateChallengeBinding
import com.hasz.gymrats.app.databinding.FragmentEditChallengeBinding
import com.hasz.gymrats.app.extension.activeOrUpcoming
import com.hasz.gymrats.app.extension.isActive
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.refreshable.Refreshable
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState
import kotlinx.android.synthetic.main.fragment_create_challenge.*
import kotlinx.android.synthetic.main.fragment_no_active_challenges.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

class EditChallengeFragment : Fragment() {
  private lateinit var startDateTime: LocalDateTime
  private lateinit var endDateTime: LocalDateTime
  private var challengeId: Int = 0

  companion object {
    fun newInstance(challenge: Challenge): ChallengeFragment {
      return ChallengeFragment().also {
        it.arguments = Bundle().also { b -> b.putParcelable("challenge", challenge) }
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val challenge: Challenge = requireArguments().getParcelable("challenge")!!

    challengeId = challenge.id
    startDateTime = challenge.start_date
    endDateTime = challenge.end_date

    return DataBindingUtil.inflate<FragmentEditChallengeBinding>(
      inflater, R.layout.fragment_edit_challenge, container, false
    ).apply {
      startDate.editText?.inputType = InputType.TYPE_NULL
      startDate.editText?.setOnClickListener {
        val picker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, y, m, d ->
          startDateTime = LocalDateTime.of(y, m + 1, d, 0, 0)
          updateStart()
        }, startDateTime.year, startDateTime.monthValue - 1, startDateTime.dayOfMonth)

        picker.show()
      }

      endDate.editText?.inputType = InputType.TYPE_NULL
      endDate.editText?.setOnClickListener {
        val picker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, y, m, d ->
          endDateTime = LocalDateTime.of(y, m + 1, d, 0, 0)
          updateEnd()
        }, endDateTime.year, endDateTime.monthValue - 1, endDateTime.dayOfMonth)

        picker.show()
      }

      createChallengeButton.setOnClickListener {
        val name = name.editText?.text?.toString() ?: ""

        if (name.isEmpty()) {
          return@setOnClickListener
        }

        createChallengeButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        GymRatsApi.editChallenge(challengeId, startDateTime, endDateTime, name, description.editText?.text.toString()) { result ->
          createChallengeButton.isEnabled = true

          result.fold(
            onSuccess = { _ ->
              GymRatsApi.allChallenges { result ->
                result.fold(
                  onSuccess = { challenges ->
                    ChallengeState.allChallenges = challenges

                    val activeOrUpcoming = challenges.activeOrUpcoming()
                    val nav = findNavController()
                    (requireContext() as MainActivity).updateNav(activeOrUpcoming)

                    nav.popBackStack()

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
                  },
                  onFailure = { error ->
                    Snackbar.make(requireView(), error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
                  }
                )
              }
            },
            onFailure = { error ->
              Snackbar.make(it, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
            }
          )
        }
      }

      startDate.editText?.setText(startDateTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")))
      endDate.editText?.setText(endDateTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")))

      name.editText?.setText(challenge.name)
      description.editText?.setText(challenge.description)
    }.root
  }

  private fun updateStart() {
    startDate.editText?.setText(startDateTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")))
  }

  private fun updateEnd() {
    endDate.editText?.setText(endDateTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")))
  }
}