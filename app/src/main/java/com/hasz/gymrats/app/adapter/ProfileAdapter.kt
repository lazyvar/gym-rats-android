package com.hasz.gymrats.app.adapter

import agency.tango.android.avatarview.views.AvatarView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.extension.buckets
import com.hasz.gymrats.app.extension.daysLeft
import com.hasz.gymrats.app.fragment.ChallengeFragmentDirections
import com.hasz.gymrats.app.fragment.ProfileFragmentDirections
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.*
import com.hasz.gymrats.app.service.AuthService
import org.threeten.bp.format.DateTimeFormatter
import java.lang.Error

class ProfileAdapter(private val challenge: Challenge, private val workouts: List<Workout>): RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
  private var rows: List<ChallengeRow> = arrayListOf()

  init {
    val sections: List<ChallengeRow> = challenge.buckets(workouts).flatMap {
      val headerSection = ChallengeRow(headerTitle = it.first.format(DateTimeFormatter.ofPattern("EEEE, MMM d")))
      val workouts = it.second.map { w -> ChallengeRow(workout = w) }
      val list = arrayListOf(headerSection)
      list.addAll(workouts)

      return@flatMap list
    }

    rows = arrayListOf()
    (rows as ArrayList<ChallengeRow>).addAll(sections)
  }

  inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val loader = GlideLoader()
    val workoutImageView: ImageView? = itemView.findViewById(R.id.workoutImageView)
    val accountName: TextView? = itemView.findViewById(R.id.accountName)
    val title: TextView? = itemView.findViewById(R.id.title)
    val avatar: AvatarView? = itemView.findViewById(R.id.avatarView)
    val time: TextView? = itemView.findViewById(R.id.time)
    val headerText: TextView? = itemView.findViewById(R.id.headerText)
  }

  override fun getItemCount(): Int = rows.size

  // 0 = no workouts
  // 1 = challenge info
  // 2 = section header
  // 3 = workout
  override fun getItemViewType(position: Int): Int {
    val row = rows[position]

    return if (row.noWorkouts) {
      0
    } else if (row.challengeInfo != null) {
      1
    } else if (row.headerTitle != null){
      2
    } else if (row.workout != null) {
      3
    } else {
      throw Error("Bad ViewType: $row")
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)

    when(viewType) {
      0 -> {
        val view = inflater.inflate(R.layout.item_challenge_header, parent, false)

        return ViewHolder(view)
      }
      1 -> {
        return if (challenge.profile_picture_url != null) {
          val view = inflater.inflate(R.layout.item_banner_with_image, parent, false)

          ViewHolder(view)
        } else {
          val view = inflater.inflate(R.layout.item_banner_no_image, parent, false)

          ViewHolder(view)
        }
      }
      2 -> {
        val view = inflater.inflate(R.layout.item_challenge_header, parent, false)

        return ViewHolder(view)
      }
      3 -> {
        val view = inflater.inflate(R.layout.item_workout, parent, false)

        return ViewHolder(view)
      }
      else -> {
        throw Error("Bad viewType: $viewType")
      }
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val row = rows[position]

    holder.itemView.setOnClickListener {  }

    if (row.noWorkouts) {

    } else if (row.challengeInfo != null) {

    } else if (row.headerTitle != null) {
      holder.headerText?.text = row.headerTitle
    } else if (row.workout != null) {
      val workout = row.workout

      Glide.with(holder.itemView.context)
        .load(workout.photo_url)
        .into(holder.workoutImageView!!)

      holder.loader.loadImage(holder.avatar!!, workout.account.profile_picture_url ?: "", workout.account.full_name)
      holder.accountName?.text = workout.account.full_name
      holder.title?.text = workout.title
      holder.time?.text = workout.occurredAt().format(DateTimeFormatter.ofPattern("h:mm a"))
      holder.itemView.setOnClickListener {
        it.findNavController().navigate(ProfileFragmentDirections.workout(workout, challenge))
      }
    } else {
      throw Error("Bad bind: $row")
    }
  }
}