package com.hasz.gymrats.app.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ranking(
  val account: Account,
  val score: String
): Parcelable