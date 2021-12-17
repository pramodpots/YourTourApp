package com.lab.assignment.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ImageDataDao {
    @Query("SELECT * from image ORDER by id ASC")
    fun getLiveItems(): LiveData<List<ImageData>>?

    @Query("SELECT * from image ORDER by id ASC")
    suspend fun getItemsASC(): List<ImageData>

    @Query("SELECT * from image ORDER by id DESC")
    suspend fun getItemsDESC(): List<ImageData>

    @Query("SELECT * from image WHERE id = :id")
    fun getItem(id: Int): ImageData

    // Specify the conflict strategy as REPLACE,
    // when the trying to add an existing Item
    // into the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(singleImageData: ImageData): Long

    @Update
    suspend fun update(imageData: ImageData)

    @Delete
    suspend fun delete(imageData: ImageData)
}