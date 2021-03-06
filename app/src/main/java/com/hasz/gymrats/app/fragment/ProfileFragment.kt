package com.hasz.gymrats.app.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.adapter.ProfileAdapter
import com.hasz.gymrats.app.databinding.FragmentChallengeBinding
import com.hasz.gymrats.app.databinding.FragmentProfileBinding
import com.hasz.gymrats.app.extension.completed
import com.hasz.gymrats.app.extension.onDay
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Account
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.Workout
import com.hasz.gymrats.app.refreshable.Refreshable
import com.hasz.gymrats.app.service.GymRatsApi
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.threeten.bp.ZoneId
import org.threeten.bp.LocalDate
import java.util.*

class ProfileFragment: Fragment(), Refreshable {
  private lateinit var profile: Account
  private var challenge: Challenge? = null
  private lateinit var viewManager: RecyclerView.LayoutManager
  private lateinit var viewAdapter: RecyclerView.Adapter<*>
  private lateinit var binding: FragmentProfileBinding

  private val loader = GlideLoader()
  private var savedView: View? = null
  private var workouts: List<Workout> = arrayListOf()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    if (savedView != null) { return savedView }

    viewManager = LinearLayoutManager(context)
    profile = requireArguments().getParcelable("profile")!!
    challenge = requireArguments().getParcelable("challenge")

    savedView = DataBindingUtil.inflate<FragmentProfileBinding>(
      inflater, R.layout.fragment_profile, container, false
    ).apply {
      binding = this
      refresh()
    }.root

    return savedView
  }

  override fun refresh() {
    binding.apply {
      body.visibility = View.GONE
      progressBar.visibility = View.VISIBLE
      recyclerView.layoutManager = viewManager

      if (challenge != null && challenge!!.completed()) {
        val date = challenge!!.end_date
        calendarView.currentDate = CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
        calendarView.selectedDate = CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
      } else {
        val date = LocalDate.now()

        calendarView.currentDate = CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
        calendarView.selectedDate = CalendarDay.from(date.year, date.monthValue, date.dayOfMonth)
      }

      calendarView.setOnDateChangedListener { _, date, _ ->
        if (challenge != null) {
          viewAdapter = ProfileAdapter(challenge!!, workouts.onDay(LocalDate.of(date.year, date.month, date.day)))
          recyclerView.adapter = viewAdapter
        } else {
          // ...
        }
      }

      calendarView.showOtherDates = MaterialCalendarView.SHOW_NONE

      if (challenge == null) {
        // ...
      } else {
        GymRatsApi.getWorkouts(profile, challenge!!) { result ->
          result.fold(
            onSuccess = {
              workouts = it
              viewAdapter = ProfileAdapter(
                challenge!!,
                it.onDay(
                  LocalDate.of(
                    calendarView.selectedDate!!.year,
                    calendarView.selectedDate!!.month,
                    calendarView.selectedDate!!.day
                  )
                )
              )

              recyclerView.adapter = viewAdapter
              body.visibility = View.VISIBLE
              progressBar.visibility = View.GONE

              calendarView.addDecorator(
                EventDecorator(
                  Color.parseColor("#D33A2C"),
                  it.map { w ->
                    CalendarDay.from(
                      w.occurred_at.atZone(ZoneId.systemDefault()).toLocalDate()
                    )
                  })
              )
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
      }
    }
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
