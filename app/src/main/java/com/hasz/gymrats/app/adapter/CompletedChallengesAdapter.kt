package com.hasz.gymrats.app.adapter

import agency.tango.android.avatarview.views.AvatarView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.application.GymRatsApplication
import com.hasz.gymrats.app.fragment.CompletedChallengesFragmentDirections
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.Challenge
import com.hasz.gymrats.app.model.SettingsRow
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat

class CompletedChallengesAdapter(private val challenges: List<Challenge>): RecyclerView.Adapter<CompletedChallengesAdapter.ViewHolder>() {
  inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
    val name: TextView? = itemView.findViewById(R.id.name)
    val description: TextView? = itemView.findViewById(R.id.description)
    val avatarView: AvatarView? = itemView.findViewById(R.id.avatarView)
    val headerText: TextView? = itemView.findViewById(R.id.headerText)
    val loader = GlideLoader()
  }

  override fun getItemCount() = if (challenges.isEmpty()) { 1 } else { challenges.size }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)

    return if (challenges.isEmpty()) {
      val view = inflater.inflate(R.layout.item_challenge_header, parent, false)

      ViewHolder(view)
    } else {
      val view = inflater.inflate(R.layout.item_completed_challenge, parent, false)

      ViewHolder(view)
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (challenges.isEmpty()) {
      holder.headerText?.text = "No completed challenges"
    } else {
      val challenge = challenges[position]

      holder.loader.loadImage(
        holder.avatarView!!,
        challenge.profile_picture_url ?: "",
        challenge.name
      )
      holder.name?.text = challenge.name
      holder.description?.text =
        challenge.end_date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
      holder.itemView.setOnClickListener {
        it.findNavController().navigate(CompletedChallengesFragmentDirections.challenge(challenge))
      }
    }
  }
}