package com.lab.assignment.data

import android.graphics.Bitmap
import androidx.room.*

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "image", indices = [Index(value = ["id","title"])])
data class ImageData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name="uri") val imageUri: String,
    @ColumnInfo(name="title") var imageTitle: String,
    @ColumnInfo(name="description") var imageDescription: String? = null,
    @ColumnInfo(name="tripTitle") var imageTripTitle: String? = null,
    @ColumnInfo(name="dateTime") var imageDateTime: String? = null,
    @ColumnInfo(name="latitude") var imageLatitude: Double,
    @ColumnInfo(name="longitude") var imageLongitude: Double,
    @ColumnInfo(name="barometricPressure") var imageBarometricPressure: String? = null,
    @ColumnInfo(name="temperature") var imageTemperature: String? = null,
    @ColumnInfo(name="thumbnailUri") var thumbnailUri: String,)
{
    @Ignore
    var thumbnail: Bitmap? = null
}