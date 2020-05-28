package com.hasz.gymrats.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.model.Workout

class ChallengeAdapter(private val workouts: List<Workout>): RecyclerView.Adapter<ChallengeAdapter.ViewHolder>() {
  inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val workoutImageView: ImageView = itemView.findViewById(R.id.workoutImageView)
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

    Glide.with(holder.itemView)
      .load(workout.photo_url)
      .fitCenter()
      .into(holder.workoutImageView)
  }
}