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
import com.hasz.gymrats.app.fragment.WorkoutFragmentDirections
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.Comment
import com.hasz.gymrats.app.model.Workout
import com.hasz.gymrats.app.model.occurredAt
import org.threeten.bp.format.DateTimeFormatter

class WorkoutAdapter(private val workout: Workout, private val challenge: Challenge, private val comments: List<Comment>): RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {
  inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
    val name: TextView? = itemView.findViewById(R.id.name)
    val descriptionLabel: TextView? = itemView.findViewById(R.id.descriptionLabel)
    val timeLabel: TextView? = itemView.findViewById(R.id.timeLabel)
    val titleLabel: TextView? = itemView.findViewById(R.id.titleLabel)
    val accountNameLabel: TextView? = itemView.findViewById(R.id.accountNameLabel)
    val avatarView: AvatarView? = itemView.findViewById(R.id.avatarView)
    val headerText: TextView? = itemView.findViewById(R.id.headerText)
    val workoutImageView: ImageView? = itemView.findViewById(R.id.workoutImageView)
    val loader = GlideLoader()
  }

  override fun getItemCount() = comments.size + 2

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_workout_details, parent, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    configureWorkoutDetails(holder)
  }

  private fun configureWorkoutDetails(holder: ViewHolder) {
    if (workout.photo_url != null && holder.workoutImageView != null) {
      Glide.with(holder.itemView.context)
        .load(workout.photo_url)
        .into(holder.workoutImageView)
    }

    val desc = arrayListOf<String>()

    if (workout.description != null) {
      desc.add("${workout.description}\n")
    }

    if (workout.duration != null) {
      desc.add("Active for ${workout.duration} minutes")
    }

    if (workout.distance != null) {
      desc.add("Traveled ${workout.distance} miles")
    }

    if (workout.steps != null) {
      desc.add("Strode ${workout.steps} steps")
    }

    if (workout.calories != null) {
      desc.add("Burned ${workout.calories} calories")
    }

    if (workout.points != null) {
      desc.add("Earned ${workout.points} points")
    }

    holder.avatarView?.setOnClickListener {
      it.findNavController().navigate(
        WorkoutFragmentDirections.profile(
          profile = workout.account,
          challenge = challenge
        )
      )
    }

    holder.accountNameLabel?.setOnClickListener {
      it.findNavController().navigate(
        WorkoutFragmentDirections.profile(
          profile = workout.account,
          challenge = challenge
        )
      )
    }

    holder.accountNameLabel?.text = workout.account.full_name
    holder.loader.loadImage(
      holder.avatarView,
      workout.account.profile_picture_url ?: "",
      workout.account.full_name
    )

    holder.titleLabel?.text = workout.title

    if (desc.isEmpty()) {
      holder.descriptionLabel?.visibility = View.GONE
    } else {
      holder.descriptionLabel?.text = desc.joinToString("\n")
    }

    holder.timeLabel?.text = workout.occurredAt().format(DateTimeFormatter.ofPattern("h:mm a"))
  }
}