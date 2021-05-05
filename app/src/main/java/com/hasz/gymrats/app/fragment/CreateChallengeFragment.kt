package com.hasz.gymrats.app.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentCreateChallengeBinding
import com.hasz.gymrats.app.model.ChallengeScoreBy
import com.hasz.gymrats.app.service.GymRatsApi
import com.hasz.gymrats.app.state.ChallengeState
import kotlinx.android.synthetic.main.activity_log_workout.*
import kotlinx.android.synthetic.main.fragment_create_challenge.*
import java.text.SimpleDateFormat
import java.util.*

class CreateChallengeFragment: Fragment(), AdapterView.OnItemSelectedListener {
  private var startDateTime: Date = Date()
  private lateinit var endDateTime: Date
  private val simpleDateFormat = SimpleDateFormat.getDateInstance()
  private var challengeScoreBy: ChallengeScoreBy = ChallengeScoreBy.WORKOUTS

  override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
    challengeScoreBy = ChallengeScoreBy.values()[pos]
  }

  override fun onNothingSelected(parent: AdapterView<*>) {
    // do nothing
  }

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

      var list = ChallengeScoreBy.values().map { it.name.toLowerCase().capitalize() }
      val spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, list)
      spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

      spinner.adapter = spinnerAdapter
      spinner.onItemSelectedListener = this@CreateChallengeFragment

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
        val picker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, y, m, dayOfMonth ->
          val calendar = Calendar.getInstance()
          calendar.set(y, m, dayOfMonth)

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

        GymRatsApi.createChallenge(startDateTime, endDateTime, name, description.editText?.text.toString(), challengeScoreBy.name.toLowerCase()) { result ->
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
