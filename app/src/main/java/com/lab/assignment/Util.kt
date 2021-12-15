package com.lab.assignment

import android.media.ExifInterface
//import androidx.exifinterface.*
import android.util.Log
//import androidx.exifinterface.media.ExifInterface
import com.lab.assignment.data.ImageData
import java.io.File
import java.io.IOException

class Util {



    fun readPhotoMetadata(img: ImageData): FloatArray {
            val file = File(img.imageUri)
            val latlng = FloatArray(2)
            try {
//                val exifInterface = ExifInterface(file.absolutePath)
                    val exif: ExifInterface = ExifInterface(file)
//                Log.i("lat",exifInterface.latLong.toString())
//                exifInterface.
//                Log.i("", exifInterface.
//                exifInterface.getLatLong(latlng);
                exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                System.out.println(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE))

                Log.i("lat", exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE).toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return latlng
        }


}