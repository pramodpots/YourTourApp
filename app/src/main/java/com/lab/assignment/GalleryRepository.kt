package com.lab.assignment

import android.app.Application
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lab.assignment.data.ImageData
import com.lab.assignment.data.ImageDataDao
import com.lab.assignment.data.ImageRoomDatabase


class GalleryRepository(application: Application) {
    private var mDBDao: ImageDataDao? = null
    private var images : MutableList<ImageData>? =null
    private var imagesList: MutableLiveData<List<ImageData>>? = null
    private val filteredImages: LiveData<List<ImageData>>? = null
    private val db: ImageRoomDatabase? = null


    init {
        val db: ImageRoomDatabase? = ImageRoomDatabase.getDatabase(application)
        if (db != null) { mDBDao = db.imageDataDao() }
        //images = mDBDao?.getItems()!!
        /* live photos from db */

    }

    fun getImages(): MutableList<ImageData>? {
        return images
    }

    fun getAllPhotos() : LiveData<List<ImageData>>?{
        return imagesList
    }

    fun insertPhoto(image: ImageData?) {
        if (image != null) {
           InsertAsyncTask(mDBDao).insertInBackground(image)
        }
    }

    fun deletePhoto(image: ImageData?) {
        if (image != null) {
            DeleteAsyncTask(mDBDao).deleteInBackground(image)
        }
    }

    fun updatePhoto(image: ImageData?) {
        if (image != null) {
            UpdateAsyncTask(mDBDao).updateInBackground(image)
        }
    }

    /*suspend fun getFilteredPhotos(
        title: String,
        description: String,
        date: String
    ): LiveData<List<ImageData>>? {
        return mDBDao?.getFilterItems(title, description, date)
    }*/

    companion object {
        private val scope = CoroutineScope(Dispatchers.IO)
        private class GetAllAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            fun getAllImagesInBackground(vararg params: ImageData): LiveData<List<ImageData>>? {
                var imagesList: LiveData<List<ImageData>>? = null
                scope.launch {
                    for(param in params){
//                        imagesList = this@GetAllAsyncTask.dao?.getItems()
                    }
                }
                return imagesList
            }
        }
        private class InsertAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            fun insertInBackground(vararg params: ImageData){
                scope.launch {
                    for(param in params){
                        val insertedId: Int? = this@InsertAsyncTask.dao?.insert(param)?.toInt()
                    }
                }
            }
        }
        private class UpdateAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
             fun updateInBackground(vararg params: ImageData) {
                scope.launch {
                    for(param in params){
                        val updateId: Unit? = this@UpdateAsyncTask.dao?.update(param)
                        // you may want to check if insertedId is null to confirm successful insertion
                        //Log.i("MyRepository", "number generated: " + param.number.toString()
                        //        + ", inserted with id: " + insertedId.toString() + "")
                    }
                }
            }
        }
        private class DeleteAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            fun deleteInBackground(vararg params: ImageData) {
                scope.launch {
                    for (param in params) {
                        val deletedData: Unit? = this@DeleteAsyncTask.dao?.delete(param)
                        // you may want to check if insertedId is null to confirm successful insertion
                        //Log.i("MyRepository", "number generated: " + param.number.toString()
                        //        + ", inserted with id: " + insertedId.toString() + "")
                    }
                }
            }
        }
    }

}