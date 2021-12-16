package com.lab.assignment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.lab.assignment.data.ImageDataDao
import kotlinx.coroutines.*

class EditActivity : AppCompatActivity() {

    lateinit var daoObj: ImageDataDao
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        var util = Util()
        daoObj = (this@EditActivity.application as ImageApplication)
            .databaseObj.imageDataDao()
        val bundle: Bundle? = intent.extras
        var position = -1

        if (bundle != null) {
            // this is the image position in the itemList
            position = bundle.getInt("position")
            if (position != -1) {
//                val imageView = findViewById<ImageView>(R.id.edit_image)
                val titleEditToolbar = findViewById<Toolbar>(R.id.editor_toolbar)
                val titleTextInput = findViewById<TextInputEditText>(R.id.edit_image_title)
                val descriptionTextInput =
                    findViewById<TextInputEditText>(R.id.edit_image_description)
                val tripTitleTextInput =
                    findViewById<TextInputEditText>(R.id.edit_image_trip_title)
                val dateTimeTextInput =
                    findViewById<TextInputEditText>(R.id.edit_image_date_time)
                val latitudeTextInput =
                    findViewById<TextInputEditText>(R.id.edit_image_latitue)
                val longitudeTextInput =
                    findViewById<TextInputEditText>(R.id.edit_image_longitude)
                val barometricTextInput =
                    findViewById<TextInputEditText>(R.id.edit_image_barometric_pressure)
                val temperatureTextInput =
                    findViewById<TextInputEditText>(R.id.edit_image_temperature)

                makeButtonListeners(position)

                MyAdapter.items[position].let {
//                    imageView.setImageBitmap(it.thumbnail)
//                    util.readPhotoMetadata(it)
                    titleEditToolbar.title = it.imageTitle
                    titleTextInput.setText(it.imageTitle)
                    descriptionTextInput.setText(it.imageDescription ?: "N/A")
                    tripTitleTextInput.setText(it.imageTripTitle ?: "N/A")
                    dateTimeTextInput.setText(it.imageDateTime ?: "N/A")
                    latitudeTextInput.setText(it.imageLatitude.toString() ?: "N/A")
                    longitudeTextInput.setText(it.imageLongitude.toString() ?: "N/A")
                    barometricTextInput.setText(it.imageBarometricPressure ?: "N/A")
                    temperatureTextInput.setText(it.imageTemperature ?: "N/A")

                    System.out.println("it.imageLatitude" + it.imageLatitude)
                }
            }
        }
    }

    private fun makeButtonListeners(position: Int) {
        var id = MyAdapter.items[position].id
        val cancelButton: Button = findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener {
            this@EditActivity.finish()
        }

        // Delete button listener
        val deleteButton: Button = findViewById(R.id.delete_button)
        deleteButton.setOnClickListener {
            scope.launch(Dispatchers.IO) {
                async { daoObj.delete(MyAdapter.items[position]) }
                    .invokeOnCompletion {
                        MyAdapter.items.removeAt(position)
                        val intent = Intent()
                            .putExtra("position", position)
                            .putExtra("id", id)
                            .putExtra("deletion_flag", 1)
                        this@EditActivity.setResult(Activity.RESULT_OK, intent)
                        this@EditActivity.finish()
                    }
            }
        }

        // Save button listener
        val saveButton: Button = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            val titleTextInput = findViewById<TextInputEditText>(R.id.edit_image_title)
            val descriptionTextInput =
                findViewById<TextInputEditText>(R.id.edit_image_description)
            val tripTitleTextInput =
                findViewById<TextInputEditText>(R.id.edit_image_trip_title)
            val dateTimeTextInput =
                findViewById<TextInputEditText>(R.id.edit_image_date_time)
            val latitudeTextInput =
                findViewById<TextInputEditText>(R.id.edit_image_latitue)
            val longitudeTextInput =
                findViewById<TextInputEditText>(R.id.edit_image_longitude)
            val barometricTextInput =
                findViewById<TextInputEditText>(R.id.edit_image_barometric_pressure)
            val temperatureTextInput =
                findViewById<TextInputEditText>(R.id.edit_image_temperature)

            MyAdapter.items[position].imageTitle = titleTextInput.text.toString()
            MyAdapter.items[position].imageDescription = descriptionTextInput.text.toString()
            MyAdapter.items[position].imageTripTitle = tripTitleTextInput.text.toString()
            MyAdapter.items[position].imageDateTime = dateTimeTextInput.text.toString()
            MyAdapter.items[position].imageLatitude = latitudeTextInput.text.toString().toDouble()
            MyAdapter.items[position].imageLongitude = longitudeTextInput.text.toString().toDouble()
            MyAdapter.items[position].imageBarometricPressure = barometricTextInput.text.toString()
            MyAdapter.items[position].imageTemperature = temperatureTextInput.text.toString()


            scope.launch(Dispatchers.IO) {
                async { daoObj.update(MyAdapter.items[position]) }
                    .invokeOnCompletion {
                        val intent = Intent()
                            .putExtra("position", position)
                            .putExtra("id", id)
                            .putExtra("deletion_flag", 0)
                        this@EditActivity.setResult(Activity.RESULT_OK, intent)
                        this@EditActivity.finish()
                    }
            }
        }
    }
}