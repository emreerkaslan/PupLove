package com.erkaslan.puplove.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.erkaslan.puplove.R

object DataBindingUtils {

    @JvmStatic
    @BindingAdapter(
        value = ["app:imageUrl"],
        requireAll = false
    )
    fun loadImageWithPlaceholder(
        imageView: ImageView,
        imageUrl: String?,
    ) {
        Glide.with(imageView.context).load(imageUrl)
            .placeholder(R.drawable.ic_dog_loading_placeholder)
            .error(R.drawable.ic_dog_loading_placeholder)
            .into(imageView)
    }
}