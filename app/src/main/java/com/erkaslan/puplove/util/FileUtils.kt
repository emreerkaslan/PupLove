package com.erkaslan.puplove.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.erkaslan.puplove.MainActivity
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    private const val favoritesFolder = "Favorites"

    fun createImageFile(activity: MainActivity, uri: String, completion:(success: Boolean, filePath: String?) -> Unit) {
        try {
            val imageUri = Uri.parse(uri)
            val inputStream = activity.contentResolver.openInputStream(imageUri)

            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputFile = File(activity.filesDir, favoritesFolder)
            val outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            completion.invoke(true, outputFile.absolutePath)
        } catch (e: Exception) {
            completion.invoke(false, null)
        }
    }

    fun deleteImageFile(filePath: String?) : Boolean {
        filePath?.let {
            val fileToBeDeleted = File(filePath)
            return if (fileToBeDeleted.exists()) { fileToBeDeleted.delete() } else false
        } ?: return false
    }
}