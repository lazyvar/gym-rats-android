package com.hasz.gymrats.app.loader

import agency.tango.android.avatarview.AvatarPlaceholder
import agency.tango.android.avatarview.ImageLoaderBase
import agency.tango.android.avatarview.views.AvatarView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hasz.gymrats.app.application.GymRatsApplication
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.utils.ShapeImageView

class GlideLoader: ImageLoaderBase, ImageLoader {
  constructor() : super() {}
  constructor(defaultPlaceholderString: String?): super(defaultPlaceholderString) {}

  override fun loadImage(
   avatarView: AvatarView,
   avatarPlaceholder: AvatarPlaceholder,
   avatarUrl: String
  ) {
    Glide.with(avatarView.context)
      .load(avatarUrl)
      .circleCrop()
      .placeholder(avatarPlaceholder)
      .into(avatarView)
  }

  override fun loadImage(imageView: ImageView?, url: String?, payload: Any?) {
    if (url == null || url.isEmpty() || imageView == null) { return }

    if (imageView is ShapeImageView) {
      Glide.with(GymRatsApplication.context!!)
        .load(url)
        .fitCenter()
        .circleCrop()
        .into(imageView)
    } else {
      Glide.with(GymRatsApplication.context!!)
        .load(url)
        .fitCenter()
        .centerCrop()
        .into(imageView)
    }
  }
}