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
import com.hasz.gymrats.app.model.Account
import com.hasz.gymrats.app.model.Challenge
import org.threeten.bp.format.DateTimeFormatter

class UpcomingChallengeAdapter(private val challenge: Challenge, private val members: List<Account>): RecyclerView.Adapter<UpcomingChallengeAdapter.ViewHolder>() {
  inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
    val loader = GlideLoader()
    val bannerImageView: ImageView? = itemView.findViewById(R.id.bannerImageView)
    val startDate: TextView? = itemView.findViewById(R.id.startDateLabel)
    val endDate: TextView? = itemView.findViewById(R.id.endDateLabel)
    val joinCode: TextView? = itemView.findViewById(R.id.joinCodeLabel)
    val scoreByLabel: TextView? = itemView.findViewById(R.id.scoreByLabel)
    val avatar: AvatarView? = itemView.findViewById(R.id.avatarView)
    val nameLabel: TextView? = itemView.findViewById(R.id.nameLabel)
  }

  override fun getItemCount() = 1 + members.size

  override fun getItemViewType(position: Int): Int {
    return if (position == 0) {
      0
    } else {
      1
    }
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)

    return if (viewType == 0) {
      if (challenge.profile_picture_url != null && challenge.profile_picture_url.isNotEmpty()) {
        val view =
          inflater.inflate(R.layout.item_upcoming_challenge_header_with_banner, parent, false)

        ViewHolder(view)
      } else {
        val view =
          inflater.inflate(R.layout.item_upcoming_challenge_header_no_banner, parent, false)

        ViewHolder(view)
      }
    } else {
      val view = inflater.inflate(R.layout.item_upcoming_member, parent, false)

      ViewHolder(view)
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (position == 0) {
      if (challenge.profile_picture_url != null) {
        Glide.with(holder.itemView.context)
          .load(challenge.profile_picture_url)
          .into(holder.bannerImageView!!)
      }

      holder.startDate?.text =
        challenge.start_date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
      holder.endDate?.text = challenge.end_date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
      holder.joinCode?.text = challenge.code
      holder.scoreByLabel?.text = description(challenge.score_by).capitalize()
    } else {
      val member = members[position - 1]

      holder.loader.loadImage(
        holder.avatar!!,
        member.profile_picture_url ?: "",
        member.name
      )
      holder.nameLabel?.text = member.name
    }
  }

  private fun description(scoreBy: String?): String {
    val scoreBy = scoreBy ?: ""

    return when (scoreBy) {
      "duration" -> "minutes"
      "distance" -> "miles"
      else -> scoreBy
    }
  }
}