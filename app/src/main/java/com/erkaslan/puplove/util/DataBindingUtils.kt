package com.erkaslan.puplove.util

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.erkaslan.puplove.R
import java.io.File

object DataBindingUtils {

    @JvmStatic
    @BindingAdapter(
        value = ["app:imageUrl", "app:imagePath"],
        requireAll = false
    )
    fun loadImageWithPlaceholder(
        imageView: ImageView,
        imageUrl: String?,
        imagePath: String?,
    ) {
        val loadSource = imagePath?.let {
            val file = File(it)
            if (file.exists()) Uri.fromFile(file)
            else imageUrl
        } ?: imageUrl

        Glide.with(imageView.context).asBitmap().load(loadSource)
            .placeholder(R.drawable.ic_dog_loading_placeholder)
            .error(R.drawable.ic_dog_loading_placeholder)
            .into(imageView)
    }
}