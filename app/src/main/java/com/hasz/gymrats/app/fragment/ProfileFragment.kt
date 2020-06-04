package com.hasz.gymrats.app.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.adapter.ChallengeAdapter
import com.hasz.gymrats.app.databinding.FragmentProfileBinding
import com.hasz.gymrats.app.extension.completed
import com.hasz.gymrats.app.extension.isActive
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Account
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.Workout
import com.hasz.gymrats.app.service.GymRatsApi
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.threeten.bp.ZoneId
import java.util.*

class ProfileFragment: Fragment() {
  private lateinit var profile: Account
  private lateinit var challenge: Challenge
  private val loader = GlideLoader()
  private var savedView: View? = null
  private var workouts: List<Workout> = arrayListOf()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (savedView != null) { return savedView }

    profile = requireArguments().getParcelable("profile")!!
    challenge = requireArguments().getParcelable("challenge")!!

    savedView = DataBindingUtil.inflate<FragmentProfileBinding>(
      inflater, R.layout.fragment_profile, container, false
    ).apply {
      body.visibility = View.GONE
      progressBar.visibility = View.VISIBLE

      if (challenge.completed()) {
        calendarView.currentDate = CalendarDay.from(challenge.end_date.toLocalDate())
      }

      calendarView.setOnDateChangedListener { widget, date, selected ->

      }

      calendarView.showOtherDates = MaterialCalendarView.SHOW_NONE

      GymRatsApi.getWorkouts(profile, challenge) { result ->
        result.fold(
          onSuccess = {
            body.visibility = View.VISIBLE
            progressBar.visibility = View.GONE

            recyclerView.adapter = ChallengeAdapter(challenge, it)
            calendarView.addDecorator(EventDecorator(Color.parseColor("#D33A2C"), it.map { w -> CalendarDay.from(w.created_at.atZone(ZoneId.systemDefault()).toLocalDate()) }))
            loader.loadImage(avatarView, profile.profile_picture_url ?: "", profile.full_name)
            nameLabel.text = profile.full_name
            totalWorkoutsLabel.text = "Total workouts: ${it.size}"
          },
          onFailure = { error ->
            Snackbar.make(
              root,
              error.message ?: "Something unpredictable happened.",
              Snackbar.LENGTH_LONG
            ).show()
          }
        )
      }
    }.root

    return savedView
  }
}

class EventDecorator(private val color: Int, dates: Collection<CalendarDay>?): DayViewDecorator {
  private val dates: HashSet<CalendarDay> = HashSet(dates)

  override fun shouldDecorate(day: CalendarDay): Boolean {
    return dates.contains(day)
  }

  override fun decorate(view: DayViewFacade) {
    view.addSpan(DotSpan(8F, color))
  }
}
