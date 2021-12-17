package com.lab.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lab.assignment.databinding.ActivityMapsImageLocationBinding

/**
 * This activity shows Image location on map
 */
class MapsImageLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsImageLocationBinding
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
    private var mDescription: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsImageLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        try {
            mLatitude = bundle!!.getDouble("mLatitude")
            mLongitude = bundle!!.getDouble("mLongitude")
            mDescription = bundle!!.getString("mDescription")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_img_location) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val locationLatLng = LatLng(mLatitude, mLongitude)
        mMap.addMarker(MarkerOptions().position(locationLatLng).title(mDescription))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 17.0f))
    }
}