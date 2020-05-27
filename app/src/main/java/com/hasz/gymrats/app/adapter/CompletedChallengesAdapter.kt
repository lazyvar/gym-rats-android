package com.hasz.gymrats.app.adapter

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hasz.gymrats.app.application.GymRatsApplication
import com.hasz.gymrats.app.model.Challenge

class CompletedChallengesAdapter(private val challenges: Array<Challenge>): RecyclerView.Adapter<CompletedChallengesAdapter.ViewHolder>() {
  class ViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val textView = TextView(GymRatsApplication.context)

    return ViewHolder(textView)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.textView.text = challenges[position].name
  }

  override fun getItemCount() = challenges.size
}