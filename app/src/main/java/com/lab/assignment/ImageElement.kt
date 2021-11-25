package com.lab.assignment

import pl.aprilapps.easyphotopicker.MediaFile

class ImageElement {
    var image = -1
    var file: MediaFile? = null

    constructor(image: Int) {
        this.image = image
    }

    constructor(fileX: MediaFile?) {
        file = fileX
    }
}
