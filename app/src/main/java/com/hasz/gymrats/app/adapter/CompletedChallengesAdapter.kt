package com.hasz.gymrats.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.model.Challenge

class CompletedChallengesAdapter(private val challenges: List<Challenge>): RecyclerView.Adapter<CompletedChallengesAdapter.ViewHolder>() {
  inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
    val name: TextView = itemView.findViewById(R.id.name)
    val description: TextView = itemView.findViewById(R.id.description)
    val imageView: ImageView = itemView.findViewById(R.id.imageView)
  }

  override fun getItemCount() = challenges.size

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_completed_challenge, parent, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val challenge = challenges[position]

    holder.name.text = challenge.name
    holder.description.text = challenge.description

    Glide.with(holder.itemView)
      .load(challenge.profile_picture_url)
      .circleCrop()
      .into(holder.imageView)
  }
}