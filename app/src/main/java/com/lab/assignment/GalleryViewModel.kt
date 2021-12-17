package com.lab.assignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.lab.assignment.data.ImageData

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private var galleryRepository: GalleryRepository = GalleryRepository(application)
    private var images: MutableList<ImageData>? = ArrayList<ImageData>()
    private var imageList :  MutableList<ImageData>? = ArrayList<ImageData>()
    private var imagesList : LiveData<List<ImageData>>? = null


    init {
        //super(application);
    }

    fun getRepository(): GalleryRepository? {
        return galleryRepository
    }

    fun getPhotos(): MutableList<ImageData>? {
        images = galleryRepository.getImages()
        imageList!!.addAll(images!!)
        return imageList
    }

    fun getAllPhotos(): LiveData<List<ImageData>>? {
        this.imagesList = (this.galleryRepository.getAllPhotos())!!
        return imagesList
    }

    fun insertPhoto(image: ImageData?) {
         galleryRepository.insertPhoto(image)
    }

    fun updatePhoto(image: ImageData?) {
        galleryRepository.updatePhoto(image)
    }

    fun deletePhoto(image: ImageData?) {
        galleryRepository.deletePhoto(image)
    }
    /*suspend fun getFilteredPhotos(
        title: String,
        description: String,
        date: String,
    ): LiveData<List<ImageData>>? {
        return galleryRepository.getFilteredPhotos(title, description, date)
    }*/

}
