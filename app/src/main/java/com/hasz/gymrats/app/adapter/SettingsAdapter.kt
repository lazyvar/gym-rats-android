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
import com.hasz.gymrats.app.model.SettingsRow
import java.text.SimpleDateFormat

class SettingsAdapter(private val rows: List<SettingsRow>): RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {
  inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
    val leftText: TextView? = itemView.findViewById(R.id.leftText)
    val rightText: TextView? = itemView.findViewById(R.id.rightText)
    val headerText: TextView? = itemView.findViewById(R.id.headerText)
  }

  override fun getItemCount() = rows.size

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)

    if (viewType == 0) {
      val view = inflater.inflate(R.layout.item_header, parent, false)

      return ViewHolder(view)
    } else {
      val view = inflater.inflate(R.layout.item_table_view_cell, parent, false)

      return ViewHolder(view)
    }
  }

  override fun getItemViewType(position: Int): Int {
    val row = rows[position]

    return if (row.headerText != null) { 0 } else { 1 }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val row = rows[position]

    if (row.headerText != null) {
      holder.headerText?.text = row.headerText ?: ""
    } else {
      holder.leftText?.text = row.leftText ?: ""
      holder.rightText?.text = row.rightText ?: ""
    }
  }
}