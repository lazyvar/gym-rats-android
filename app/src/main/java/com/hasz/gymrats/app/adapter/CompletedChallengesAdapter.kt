package com.hasz.gymrats.app.adapter

import agency.tango.android.avatarview.views.AvatarView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.application.GymRatsApplication
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Challenge
import java.text.SimpleDateFormat

class CompletedChallengesAdapter(private val challenges: List<Challenge>): RecyclerView.Adapter<CompletedChallengesAdapter.ViewHolder>() {
  inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
    val name: TextView = itemView.findViewById(R.id.name)
    val description: TextView = itemView.findViewById(R.id.description)
    val avatarView: AvatarView = itemView.findViewById(R.id.avatarView)
    val loader = GlideLoader()
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
    val dateFormatter = SimpleDateFormat("MMMM d, yyyy")
    val completed = dateFormatter.format(challenge.end_date).toString()

    holder.loader.loadImage(holder.avatarView, challenge.profile_picture_url ?: "", challenge.name)
    holder.name.text = challenge.name
    holder.description.text = GymRatsApplication.context!!.getString(R.string.completed_challenge_description, completed)
  }
}