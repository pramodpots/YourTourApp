package com.lab.assignment

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lab.assignment.data.ImageDataDao
import kotlinx.coroutines.*

/**
 * Shows single image with its metadata
 * Buttons to view same image on map and edit metadata
 */
class ShowImageActivity : AppCompatActivity() {
    lateinit var daoObj: ImageDataDao

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val position = result.data?.getIntExtra("position", -1)
            val id = result.data?.getIntExtra("id", -1)
            val del_flag = result.data?.getIntExtra("deletion_flag", -1)
            var intent = Intent().putExtra("position", position)
                .putExtra("id", id).putExtra("deletion_flag", del_flag)
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    this.setResult(result.resultCode, intent)
                    this.finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)
        val bundle: Bundle? = intent.extras
        var position = -1

        daoObj = (this@ShowImageActivity.application as ImageApplication)
            .databaseObj.imageDataDao()

        if (bundle != null) {
            // this is the image position in the itemList
            position = bundle.getInt("position")
            displayData(position)
        }
    }

    // shows image and details
    private fun displayData(position: Int) {
        if (position != -1) {
            // get all ui fields
            val imageView = findViewById<ImageView>(R.id.show_image)
            val titleToolbar = findViewById<Toolbar>(R.id.show_toolbar)

            val tvDescription = findViewById<TextView>(R.id.tvDescription)
            val tvTripTitle = findViewById<TextView>(R.id.tvTripTitle)
            val tvDateTime = findViewById<TextView>(R.id.tvDateTime)
            val tvLatitude = findViewById<TextView>(R.id.tvLatitude)
            val tvLongitude = findViewById<TextView>(R.id.tvLongitude)
            val tvBarometricPressure = findViewById<TextView>(R.id.tvBarometricPressure)
            val tvTemperature = findViewById<TextView>(R.id.tvTemperature)

            val imageData = MyAdapter.items[position]

            // set image data to ui fields
            imageView.setImageBitmap(MyAdapter.items[position].thumbnail!!)
            titleToolbar.title = MyAdapter.items[position].imageTitle
            tvDescription.text = "Description: " + imageData.imageDescription
            tvTripTitle.text = "TripTitle: " + imageData.imageTripTitle
            tvDateTime.text = "DateTime: " + imageData.imageDateTime
            tvLatitude.text = "Latitude: " + imageData.imageLatitude
            tvLongitude.text = "Longitude: " + imageData.imageLongitude
            tvBarometricPressure.text = "Barometric: " + imageData.imageBarometricPressure
            tvTemperature.text = "Temperature: " + imageData.imageTemperature

            // button to jump to edit metadata activity
            val fabEdit: FloatingActionButton = findViewById(R.id.fab_edit)
            fabEdit.setOnClickListener(View.OnClickListener {
                startForResult.launch(
                    Intent(this, EditActivity::class.java).apply {
                        putExtra("position", position)
                    }
                )
            })

            // button to jump to view image on map activity
            val fabMap: FloatingActionButton = findViewById(R.id.fab_image_location)
            fabMap.setOnClickListener(View.OnClickListener {
                startForResult.launch(
                    Intent(this, MapsImageLocationActivity::class.java).apply {
                        // only sending required fields to new activity
                        putExtra("mLatitude", imageData.imageLatitude.toDouble())
                        putExtra("mLongitude", imageData.imageLongitude.toDouble())
                        putExtra(
                            "mDescription",
                            imageData.imageTripTitle + " | " + imageData.imageDescription
                        )
                    }
                )
            })
        }
    }
}
