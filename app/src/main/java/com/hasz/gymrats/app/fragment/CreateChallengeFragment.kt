package com.hasz.gymrats.app.fragment

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.databinding.FragmentCreateChallengeBinding
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

      startButton.setOnClickListener {
        
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
