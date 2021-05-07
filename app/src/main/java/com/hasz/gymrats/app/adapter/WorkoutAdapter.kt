package com.hasz.gymrats.app.adapter

import agency.tango.android.avatarview.views.AvatarView
import android.text.format.DateUtils.getRelativeTimeSpanString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.fragment.WorkoutFragment
import com.hasz.gymrats.app.fragment.WorkoutFragmentDirections
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.*
import com.hasz.gymrats.app.service.AuthService
import org.threeten.bp.format.DateTimeFormatter

class WorkoutAdapter(private val fragment: WorkoutFragment, private val workout: Workout, private val challenge: Challenge, private val comments: List<Comment>): RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {
  inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
    val name: TextView? = itemView.findViewById(R.id.name)
    val descriptionLabel: TextView? = itemView.findViewById(R.id.descriptionLabel)
    val timeLabel: TextView? = itemView.findViewById(R.id.timeLabel)
    val titleLabel: TextView? = itemView.findViewById(R.id.titleLabel)
    val accountNameLabel: TextView? = itemView.findViewById(R.id.accountNameLabel)
    val commentNameLabel: TextView? = itemView.findViewById(R.id.commentNameLabel)
    val contentLabel: TextView? = itemView.findViewById(R.id.contentLabel)
    val editTextComment: EditText? = itemView.findViewById(R.id.editTextComment)
    val submitButton: Button? = itemView.findViewById(R.id.submitButton)
    val avatarView: AvatarView? = itemView.findViewById(R.id.avatarView)
    val workoutImageView: ImageView? = itemView.findViewById(R.id.workoutImageView)
    val loader = GlideLoader()
  }

  override fun getItemCount() = comments.size + 2

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    return when(viewType) {
      0 -> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_workout_details, parent, false)

        return ViewHolder(view)
      }
      1 -> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_comment, parent, false)

        return ViewHolder(view)
      }
      2 -> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_new_comment, parent, false)

        return ViewHolder(view)
      }
      else -> {
        error("Doh!")
      }
    }
  }

  override fun getItemViewType(position: Int): Int {
    return when(position) {
      0 -> 0
      comments.size + 1 -> 2
      else -> 1
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    return when(getItemViewType(position)) {
      0 -> {
        configureWorkoutDetails(holder)
      }
      1 -> {
        configureComment(holder, comments[position - 1])
      }
      2 -> {
        configureNewComment(holder)
      }
      else -> {
        error("Doh!")
      }
    }
    configureWorkoutDetails(holder)
  }

  private fun configureNewComment(holder: ViewHolder) {
    if (holder.avatarView != null) {
      holder.loader.loadImage(holder.avatarView, AuthService.currentAccount?.profile_picture_url ?: "", AuthService.currentAccount?.full_name ?: "")
    }

    holder.submitButton?.setOnClickListener {
      val content = (holder.editTextComment?.text ?: "").trim().toString()

      if (content.isEmpty()) { return@setOnClickListener }

      fragment.postComment(content)
    }
  }

  private fun configureComment(holder: ViewHolder, comment: Comment) {
    if (holder.avatarView != null) {
      holder.loader.loadImage(holder.avatarView, comment.account.profile_picture_url ?: "", workout.account.full_name)
    }

    val timeString = getRelativeTimeSpanString(comment.created_at.toEpochMilli())

    holder.itemView.setOnClickListener {
      it.findNavController().navigate(
        WorkoutFragmentDirections.profile(
          profile = comment.account,
          challenge = challenge
        )
      )
    }

    holder.timeLabel?.text = timeString
    holder.contentLabel?.text = comment.content
    holder.commentNameLabel?.text = comment.account.full_name
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