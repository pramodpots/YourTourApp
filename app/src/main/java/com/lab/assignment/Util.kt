package com.lab.assignment

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Class to outsource utility functionality
 */
class Util {
    companion object {
        /**
         *  creates thumbnail from given image and returns minified bitmap
         *  saves this thumbnail to given thumbnail location
         *  @param imagePath absolute path of image
         *  @param thumbnailPath absolute path where thumbnail needs to be stored
         *  @return bitmap thumbnail
         */

        fun createBitmapThumbnail(imagePath: String, thumbnailPath: String): Bitmap? {
            val originalBitmap = BitmapFactory.decodeFile(imagePath)
            if (originalBitmap == null) {
                Log.e("Image Error", "Image not found, cannot create bitmap thumbnail")
                return null
            }
            // get small sized bitmap from original bitmap
            val thumbnailBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, true)

            try {
                FileOutputStream(thumbnailPath).use { out ->
                    thumbnailBitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        80,
                        out
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return thumbnailBitmap
        }

        /**
         * create a path for new thumbnail in cache
         * @param context application context
         * @return path of a file in cache where actual thumbnail will be stored
         */
        fun getNewThumbnailPath(context: Context): String {
            try {
                val directoryThumbnail = File(context.cacheDir, "thumbnails")
                val fileThumbnail = File(directoryThumbnail, UUID.randomUUID().toString())
                return fileThumbnail.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        fun checkIfCameraHardwarePresent(context: Context): Boolean {
            return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
        }
    }
}