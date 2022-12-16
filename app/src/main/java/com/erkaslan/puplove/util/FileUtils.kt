package com.erkaslan.puplove.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    private const val favoritesFolder = "/Favorites"

    fun createImageFile(
        context: Context,
        uri: String,
        completion: (success: Boolean, filePath: String?) -> Unit
    ) {
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .into(object : SimpleTarget<Bitmap>(1920, 1080) {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    saveImageFile(bitmap, completion)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    completion.invoke(false, null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    super.onLoadCleared(placeholder)
                    completion.invoke(false, null)
                }
            })
    }

    fun saveImageFile(bitmap: Bitmap, completion: (success: Boolean, filePath: String?) -> Unit) {
        val savedImagePath: String
        val imageFileName = System.currentTimeMillis().toString() + ".jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ).toString() + favoritesFolder
        )
        var success = true
        if (!storageDir.exists()) success = storageDir.mkdirs()

        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                completion.invoke(true, savedImagePath)
            } catch (e: Exception) {
                e.printStackTrace()
                completion.invoke(false, null)
            }
        }
    }

    fun deleteImageFile(filePath: String?): Boolean {
        filePath?.let {
            val fileToBeDeleted = File(filePath)
            return if (fileToBeDeleted.exists()) {
                fileToBeDeleted.delete()
            } else false
        } ?: return false
    }
}