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
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.activity.MainActivity
import com.hasz.gymrats.app.databinding.FragmentCreateChallengeBinding
import com.hasz.gymrats.app.extension.activeOrUpcoming
import com.hasz.gymrats.app.extension.isActive
import com.hasz.gymrats.app.service.AuthService
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState
import kotlinx.android.synthetic.main.fragment_create_challenge.*
import kotlinx.android.synthetic.main.fragment_no_active_challenges.*
import java.text.SimpleDateFormat
import java.util.*

class CreateChallengeFragment : Fragment() {
  private var startDateTime: Date = Date()
  private lateinit var endDateTime: Date
  private val simpleDateFormat = SimpleDateFormat.getDateInstance()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val calendar: Calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 30)

    endDateTime = calendar.time

    return DataBindingUtil.inflate<FragmentCreateChallengeBinding>(
      inflater, R.layout.fragment_create_challenge, container, false
    ).apply {
      toolbar.setNavigationOnClickListener {
        (context as Activity).finish()
      }

      startDate.editText?.inputType = InputType.TYPE_NULL
      startDate.editText?.setOnClickListener {
        val cal: Calendar = Calendar.getInstance()
        cal.time = startDateTime

        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val month: Int = cal.get(Calendar.MONTH)
        val year: Int = cal.get(Calendar.YEAR)
        val picker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
          val calendar = Calendar.getInstance()
          calendar.set(year, month, dayOfMonth)

          startDateTime = calendar.time
          updateStart()
        }, year, month, day)

        picker.show()
      }

      endDate.editText?.inputType = InputType.TYPE_NULL
      endDate.editText?.setOnClickListener {
        val cal: Calendar = Calendar.getInstance()
        cal.time = endDateTime

        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val month: Int = cal.get(Calendar.MONTH)
        val year: Int = cal.get(Calendar.YEAR)
        val picker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
          val calendar = Calendar.getInstance()
          calendar.set(year, month, dayOfMonth)

          endDateTime = calendar.time
          updateEnd()
        }, year, month, day)

        picker.show()
      }

      startDate.editText?.setText(simpleDateFormat.format(startDateTime))
      endDate.editText?.setText(simpleDateFormat.format(endDateTime))

      createChallengeButton.setOnClickListener {
        val name = name.editText?.text?.toString() ?: ""

        if (name.isEmpty()) {
          return@setOnClickListener
        }

        createChallengeButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        GymRatsApi.createChallenge(startDateTime, endDateTime, name, description.editText?.text.toString(), "workouts") { result ->
          createChallengeButton.isEnabled = true

          result.fold(
            onSuccess = { challenge ->
              ChallengeState.lastOpenedChallengeId = challenge.id
              activity?.setResult(54321)
              activity?.finish()
            },
            onFailure = { error ->
              Snackbar.make(it, error.message ?: "Something unpredictable happened.", Snackbar.LENGTH_LONG).show()
            }
          )
        }
      }
    }.root
  }

  private fun updateStart() {
    startDate.editText?.setText(simpleDateFormat.format(startDateTime))
  }

  private fun updateEnd() {
    endDate.editText?.setText(simpleDateFormat.format(endDateTime))
  }
}
