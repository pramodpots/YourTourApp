package com.lab.assignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.lab.assignment.data.ImageData

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private var galleryRepository: GalleryRepository = GalleryRepository(application)
    private var images: MutableList<ImageData>? = ArrayList<ImageData>()
    private var imageList: MutableList<ImageData>? = ArrayList<ImageData>()
    private var imagesList: LiveData<List<ImageData>>? = null


    init {
        //super(application);
    }

    fun getRepository(): GalleryRepository? {
        return galleryRepository
    }


    fun getImages(): MutableList<ImageData>? {
        images = galleryRepository.getImages()
        imageList!!.addAll(images!!)
        return imageList
    }


    fun getAllImages(): LiveData<List<ImageData>>? {
        this.imagesList = (this.galleryRepository.getAllImages())!!
        return imagesList
    }


    fun insertImage(image: ImageData?) {
        galleryRepository.insertImage(image)
    }


    fun updateImage(image: ImageData?) {
        galleryRepository.updateImage(image)
    }


    fun deletePhoto(image: ImageData?) {
        galleryRepository.deleteImage(image)
    }
}
