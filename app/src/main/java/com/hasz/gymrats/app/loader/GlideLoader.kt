package com.hasz.gymrats.app.loader

import agency.tango.android.avatarview.AvatarPlaceholder
import agency.tango.android.avatarview.ImageLoaderBase
import agency.tango.android.avatarview.views.AvatarView
import com.bumptech.glide.Glide

class GlideLoader : ImageLoaderBase {
  constructor() : super() {}
  constructor(defaultPlaceholderString: String?): super(defaultPlaceholderString) {}

  override fun loadImage(
   avatarView: AvatarView,
   avatarPlaceholder: AvatarPlaceholder,
   avatarUrl: String
  ) {
    Glide.with(avatarView.context)
      .load(avatarUrl)
      .centerCrop()
      .placeholder(avatarPlaceholder)
      .fitCenter()
      .into(avatarView)
  }
}