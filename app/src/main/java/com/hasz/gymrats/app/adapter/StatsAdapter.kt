package com.hasz.gymrats.app.adapter

import agency.tango.android.avatarview.views.AvatarView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hasz.gymrats.app.R
import com.hasz.gymrats.app.loader.GlideLoader
import com.hasz.gymrats.app.model.StatsRow

class StatsAdapter(private val rows: List<StatsRow>): RecyclerView.Adapter<StatsAdapter.ViewHolder>() {
  inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val avatar: AvatarView? = itemView.findViewById(R.id.avatarView)
    val nameLabel: TextView? = itemView.findViewById(R.id.nameLabel)
    val scoreLabel: TextView? = itemView.findViewById(R.id.scoreLabel)

    val loader = GlideLoader()
  }

  override fun getItemCount() = rows.size

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): StatsAdapter.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)

    return if (viewType == 0) {
      val view = inflater.inflate(R.layout.item_header, parent, false)

      ViewHolder(view)
    } else {
      val view = inflater.inflate(R.layout.item_ranking, parent, false)

      ViewHolder(view)
    }
  }

  override fun getItemViewType(position: Int): Int {
    val row = rows[position]

    return if (row.challenge != null) {
      0
    } else {
      1
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val row = rows[position]

    if (row.challenge != null) {
      // ...
    } else if (row.ranking != null) {
      holder.loader.loadImage(holder.avatar!!, row.ranking.account.profile_picture_url ?: "", row.ranking.account.name)
      holder.nameLabel?.text = row.ranking.account.name
      holder.scoreLabel?.text = row.ranking.score
    }
  }
}