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
    private var imageDataDao: ImageDataDao? = null
    private var mutableImageList: MutableList<ImageData>? = null
    private var mutableLiveDataImages: MutableLiveData<List<ImageData>>? = null

    init {
        val db: ImageRoomDatabase? = ImageRoomDatabase.getDatabase(application)
        if (db != null) {
            imageDataDao = db.imageDataDao()
        }
    }

    fun getImages(): MutableList<ImageData>? {
        return mutableImageList
    }

    fun getAllImages(): LiveData<List<ImageData>>? {
        return mutableLiveDataImages
    }

    fun insertImage(image: ImageData?) {
        if (image != null) {
            InsertAsyncTask(imageDataDao).insertInBackground(image)
        }
    }

    fun updateImage(image: ImageData?) {
        if (image != null) {
            UpdateAsyncTask(imageDataDao).updateInBackground(image)
        }
    }

    fun deleteImage(image: ImageData?) {
        if (image != null) {
            DeleteAsyncTask(imageDataDao).deleteInBackground(image)
        }
    }

    companion object {
        private val scope = CoroutineScope(Dispatchers.IO)

        private class GetAllAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            fun getAllImagesInBackground(vararg params: ImageData): LiveData<List<ImageData>>? {
                var imagesList: LiveData<List<ImageData>>? = null
                scope.launch {
                    for (param in params) {
                        // imagesList = this@GetAllAsyncTask.dao?.getItems()
                    }
                }
                return imagesList
            }
        }

        private class InsertAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            fun insertInBackground(vararg params: ImageData) {
                scope.launch {
                    for (param in params) {
                        val insertedId: Int? = this@InsertAsyncTask.dao?.insert(param)?.toInt()
                    }
                }
            }
        }

        private class DeleteAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            fun deleteInBackground(vararg params: ImageData) {
                scope.launch {
                    for (param in params) {
                        val deletedData: Unit? = this@DeleteAsyncTask.dao?.delete(param)
                    }
                }
            }
        }

        private class UpdateAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            fun updateInBackground(vararg params: ImageData) {
                scope.launch {
                    for (param in params) {
                        val updateId: Unit? = this@UpdateAsyncTask.dao?.update(param)
                    }
                }
            }
        }
    }

}