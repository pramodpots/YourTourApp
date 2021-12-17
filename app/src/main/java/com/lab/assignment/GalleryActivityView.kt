package com.lab.assignment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lab.assignment.data.ImageData
import com.lab.assignment.data.ImageDataDao
import kotlinx.coroutines.*
import pl.aprilapps.easyphotopicker.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import java.util.ArrayList

/**
 * Activity which shows thumbnails of images in grid view
 * User can navigate to show image activity on clicking of thumbnail
 */
class GalleryActivityView : AppCompatActivity() {
    private var myDataset: MutableList<ImageData> = ArrayList<ImageData>()
    private lateinit var daoObj: ImageDataDao
    private lateinit var mAdapter: Adapter<RecyclerView.ViewHolder>
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var easyImage: EasyImage
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tripTitle: String
    private var mPressureValue: Float = 0.0f
    private var mTemperatureValue: Float = 0.0f
    private var galleryViewModel: GalleryViewModel? = null

    companion object {
        private const val REQUEST_READ_EXTERNAL_STORAGE = 2987
        private const val REQUEST_WRITE_EXTERNAL_STORAGE = 7829
        private const val REQUEST_CAMERA_CODE = 100
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val pos = result.data?.getIntExtra("position", -1)!!
                val id = result.data?.getIntExtra("id", -1)!!
                val del_flag = result.data?.getIntExtra("deletion_flag", -1)!!
                if (pos != -1 && id != -1) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        when (del_flag) {
                            -1, 0 -> mAdapter.notifyDataSetChanged()
                            else -> mAdapter.notifyItemRemoved(pos)
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        // for getting location updates
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // get sensor data from sensor service through broadcast
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(sensorValueReceiver, IntentFilter("sensorIntent"))

        initData()

        this.galleryViewModel = ViewModelProvider(this)[GalleryViewModel::class.java]

        mRecyclerView = findViewById(R.id.grid_recycler_view)
        // set up the RecyclerView
        val numberOfColumns = 4
        mRecyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        mAdapter = MyAdapter(myDataset) as Adapter<RecyclerView.ViewHolder>
        mRecyclerView.adapter = mAdapter

        // get trip title from previous activity from inten extras
        val extras = intent.extras
        if (extras != null) {
            tripTitle = extras.getString("tripTitle").toString()
        }

        // required by Android 6.0 +
        checkPermissions(applicationContext)

        // initialize easy image library
        initEasyImage()

        // the floating button that will allow us to get the images from the Gallery
        // or open camera based on user input
        val fabGallery: FloatingActionButton = findViewById(R.id.fab_gallery)
        fabGallery.setOnClickListener(View.OnClickListener {
            easyImage.openChooser(this@GalleryActivityView)
        })

        // start sensor service
        val intent = Intent(applicationContext, SensorDataService::class.java)
        startService(intent)
    }

    /**
     * it initialises EasyImage
     */
    private fun initEasyImage() {
        easyImage = EasyImage.Builder(this)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(true)
            .build()
    }

    /**
     * Init data by loading from the database
     */
    private fun initData() {

        GlobalScope.launch {
            daoObj = (this@GalleryActivityView.application as ImageApplication)
                .databaseObj.imageDataDao()
            myDataset.addAll(daoObj.getItemsASC())
        }
    }

    /**
     * insert a ImageData into the database
     * Called for each image the user adds by clicking the fab button
     * Then retrieves the same image so we can have the automatically assigned id field
     */
    private fun insertData(imageData: ImageData): Int = runBlocking {
        var insertJob = async { daoObj.insert(imageData) }
        insertJob.await().toInt()
    }

    /**
     * check permissions are necessary starting from Android 6
     * if you do not set the permissions, the activity will simply not work and you will be probably baffled for some hours
     * until you find a note on StackOverflow
     * @param context the calling context
     */
    private fun checkPermissions(context: Context) {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder: AlertDialog.Builder =
                        AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("External storage permission is necessary")
                    alertBuilder.setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { _, _ ->
                            ActivityCompat.requestPermissions(
                                context as Activity, arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ), REQUEST_READ_EXTERNAL_STORAGE
                            )
                        })
                    val alert: AlertDialog = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("Writing external storage permission is necessary")
                    alertBuilder.setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { _, _ ->
                            ActivityCompat.requestPermissions(
                                context as Activity, arrayOf(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ), REQUEST_WRITE_EXTERNAL_STORAGE
                            )
                        })
                    val alert: AlertDialog = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_WRITE_EXTERNAL_STORAGE
                    )
                }
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_CODE
                );
            }
        }
    }

    /**
     * Receive data from sensor service and update it in local variable for further access
     */
    private val sensorValueReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            if (bundle != null) {
                mPressureValue = bundle.getString("mPressureValue").toString().toFloat()
                mTemperatureValue = bundle.getString("mTemperatureValue").toString().toFloat()
            }
        }
    }

    /**
     * When easy image library returns images save to database
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        easyImage.handleActivityResult(requestCode, resultCode, data, this,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    onPhotosReturned(imageFiles)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    super.onImagePickerError(error, source)
                }

                override fun onCanceled(source: MediaSource) {
                    super.onCanceled(source)
                }
            })
    }

    /**
     * add the selected images to the grid
     * @param returnedPhotos
     */
    private fun onPhotosReturned(returnedPhotos: Array<MediaFile>) {
        getImageData(returnedPhotos)
    }

    /**
     * given a list of photos, it creates a list of ImageData objects
     * we do not know how many elements we will have
     * it adds current location and sensor details to image data to be saved
     * @return
     */
    @SuppressLint("MissingPermission")
    private fun getImageData(returnedPhotos: Array<MediaFile>) {
        val imageDataList: MutableList<ImageData> = ArrayList<ImageData>()

        for (mediaFile in returnedPhotos) {
            val fileNameAsTempTitle = mediaFile.file.name
            if (tripTitle.isEmpty()) { // If no trip title present entry dummy
                tripTitle = "Add Trip Title"
            }
            // get current timestamp
            var datetime = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now())

            // get uri for thumbnail
            var thumbURI = Util.getNewThumbnailPath(applicationContext)

            // create instance of ImageData with basic details
            var imageData = ImageData(
                imageTitle = fileNameAsTempTitle,
                imageDescription = "Add Description",
                imageTripTitle = tripTitle,
                imageDateTime = datetime.toString(),
                imageLatitude = 0.0,
                imageLongitude = 0.0,
                imageBarometricPressure = this.mPressureValue.toString(),
                imageTemperature = this.mTemperatureValue.toString(),
                imageUri = mediaFile.file.absolutePath,
                thumbnailUri = thumbURI
            )

            // update location details when received latest location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    var imgLat = location?.latitude!!
                    var imgLng = location?.longitude!!
                    imageData.imageLatitude = imgLat
                    imageData.imageLongitude = imgLng
                    var id = insertData(imageData)
                    imageData.id = id
                    imageDataList.add(imageData)

                    // add image to database
                    myDataset.add(imageData)

                    // we tell the adapter that the data is changed and hence the grid needs
                    mAdapter.notifyDataSetChanged()
                    mRecyclerView.scrollToPosition(returnedPhotos.size - 1)
                }
        }
    }
}