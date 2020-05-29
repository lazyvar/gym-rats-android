package com.hasz.gymrats.app.adapter

import agency.tango.android.avatarview.views.AvatarView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Workout
import java.text.SimpleDateFormat

class ChallengeAdapter(private val workouts: List<Workout>): RecyclerView.Adapter<ChallengeAdapter.ViewHolder>() {
  inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val workoutImageView: ImageView = itemView.findViewById(R.id.workoutImageView)
    val accountName: TextView = itemView.findViewById(R.id.accountName)
    val title: TextView = itemView.findViewById(R.id.title)
    val avatar: AvatarView = itemView.findViewById(R.id.avatarView)
    val time: TextView = itemView.findViewById(R.id.time)
    val loader = GlideLoader()
  }

  override fun getItemCount() = workouts.size

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_workout, parent, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val workout = workouts[position]
    val dateFormatter = SimpleDateFormat("h:mm a")

    Glide.with(holder.itemView.context)
      .load(workout.photo_url)
      .into(holder.workoutImageView)

    holder.loader.loadImage(holder.avatar, workout.account.profile_picture_url ?: "", workout.account.full_name)
    holder.accountName.text = workout.account.full_name
    holder.title.text = workout.title
    holder.time.text =  dateFormatter.format(workout.created_at).toString()
  }
}