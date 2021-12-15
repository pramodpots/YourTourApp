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
    @ColumnInfo(name="description") var imageTripTitle: String? = null, // trip title
    @ColumnInfo(name="lat") var imageLatitude: Double,
    @ColumnInfo(name="lng") var imageLongitude: Double,
    @ColumnInfo(name="barometricPressure") var imageBarometricPressure: String? = null,
    @ColumnInfo(name="temperature") var imageTemperature: String? = null,
    @ColumnInfo(name="thumbnailUri") var thumbnailUri: String? = null,)
{
    @Ignore
    var thumbnail: Bitmap? = null


}