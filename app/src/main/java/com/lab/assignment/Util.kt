package com.lab.assignment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class Util {
    companion object {
        /**
         * Make a Thumbnail 100x100 in dimensions
         * @param photoPath Path of the photo
         * @param thumbPath Path of the thumbnail
         * @return Image
         */
        fun makeThumbnail(photoPath: String, thumbPath: String): Bitmap? {
            /* generate 100x100 thumbnail */
            println("Building thumbnail for file $photoPath")
            val originalBitmap = BitmapFactory.decodeFile(photoPath)
            if (originalBitmap == null) {
                println("Error loading file $photoPath, cannot build thumbnail")
                return null
            }
            val thumbnailBitmap = Bitmap.createScaledBitmap(originalBitmap, 150, 150, true)
            try {
                FileOutputStream(thumbPath).use { out ->
                    thumbnailBitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        80,
                        out
                    )
                }
            } catch (e: IOException) {
                System.err.println("Error writing thumbail to file: $thumbPath")
                e.printStackTrace()
            }
            return thumbnailBitmap
        }

        /**
         * Generate a new Thumbnail path
         * @param context Context
         * @return New thumbnail path
         */
        fun getNewThumbnailPath(context: Context): String {
            val thumbnailDir = File(context.cacheDir, "thumbnails")
            val thumbnailFile = File(thumbnailDir, UUID.randomUUID().toString())
            return thumbnailFile.absolutePath
        }
    }
}