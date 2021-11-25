package com.lab.assignment

import android.app.Application
import com.lab.assignment.data.ImageRoomDatabase

class ImageApplication: Application() {
    val databaseObj: ImageRoomDatabase by lazy { ImageRoomDatabase.getDatabase(this) }
}
