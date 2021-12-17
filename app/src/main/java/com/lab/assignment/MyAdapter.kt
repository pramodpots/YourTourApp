package com.lab.assignment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lab.assignment.data.ImageData
import kotlinx.coroutines.*

class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private lateinit var context: Context

    constructor(items: List<ImageData>) : super() {
        MyAdapter.items = items as MutableList<ImageData>
    }

    constructor(cont: Context, items: List<ImageData>) : super() {
        MyAdapter.items = items as MutableList<ImageData>
        context = cont
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate the layout, initialize the View Holder
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_image,
            parent, false
        )

        val holder: MyAdapter.ViewHolder = ViewHolder(v)
        context = parent.context
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var imageData = items[position]
        if (items[position].thumbnail == null) {
            items[position].let {
                scope.launch {
                    val bitmap =
                        decodeSampledBitmapFromResource(it.imageUri, 100, 100)
                    bitmap?.let {
                        items[position].thumbnail = it
                        holder.imageView.setImageBitmap(items[position].thumbnail)
                    }
                }
            }
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            val mainActivityContext = context as GalleryActivityView
            mainActivityContext.startForResult.launch(
                Intent(context, ShowImageActivity::class.java).apply {
                    putExtra("position", position)
                }
            )
        })
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById<View>(R.id.image_item) as ImageView

    }

    companion object {
        lateinit var items: MutableList<ImageData>
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        /**
         * helper function to generate a bitmap object of a given size from an image's file path.
         */
        suspend fun decodeSampledBitmapFromResource(
            filePath: String,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            val originalBitmap = BitmapFactory.decodeFile(filePath)
            return Bitmap.createScaledBitmap(originalBitmap, reqWidth, reqHeight, true)
        }

        /**
         * helper function to calculate an inSampleSize for resampling to achieve the right picture
         * density for a smaller size file.
         * See inSampleFile: https://developer.android.com/reference/android/graphics/BitmapFactory.Options#inSampleSize
         */
        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight;
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight = (height / 2).toInt()
                val halfWidth = (width / 2).toInt()

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth
                ) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize.toInt();
        }
    }
}
