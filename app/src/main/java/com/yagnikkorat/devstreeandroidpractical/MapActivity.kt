package com.yagnikkorat.devstreeandroidpractical

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.yagnikkorat.devstreeandroidpractical.MainActivity.Companion.FLAG_KEY
import com.yagnikkorat.devstreeandroidpractical.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val ADD_ADDRESS_KEY = "addAddress"
    }

    private var googleMap: GoogleMap? = null
    private var saveAddress: SaveAddressModel? = null
    private lateinit var binding: ActivityMapBinding

    private var flagIntent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        flagIntent = intent.getStringExtra(FLAG_KEY) ?: ""

        initMap()
        initAutocomplete()
        setUpClickListener()
    }

    private fun initAutocomplete() {

        val apiKey = "AIzaSyBSNyp6GQnnKlrMr7hD2HGiyF365tFlK5U"

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        val autocompleteSupportFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?

        autocompleteSupportFragment?.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
            )
        )

        autocompleteSupportFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {

                when (flagIntent) {
                    MainActivity.FlagAddress.UPDATE.name -> {
                        val update = intent?.getSerializableExtra(MainActivity.UPDATE_ADDRESS_KEY)
                        if (update is SaveAddressModel) {
                            saveAddress = SaveAddressModel(
                                update.id,
                                place.name,
                                place.address,
                                0.0,
                                place.latLng?.latitude,
                                place.latLng?.longitude
                            )
                        }
                    }

                    MainActivity.FlagAddress.NORMAL.name -> {
                        saveAddress = SaveAddressModel(
                            0,
                            place.name,
                            place.address,
                            0.0,
                            place.latLng?.latitude,
                            place.latLng?.longitude
                        )
                    }
                }



                Log.d(
                    "MainActivity", "onPlaceSelected: ${saveAddress.toString()}"
                )

                saveAddress?.let {
                    if (googleMap != null && place.latLng != null) {
                        googleMap?.clear()
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                place.latLng!!, 10F
                            )
                        )
                        googleMap?.addMarker(
                            MarkerOptions().title(it.name).position(place.latLng!!)
                        )
                    }
                    binding.btnSaveAddress.visibility = View.VISIBLE
                }
            }

            override fun onError(status: Status) {
                binding.btnSaveAddress.visibility = View.GONE
                Toast.makeText(applicationContext, "Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapActivity)
    }

    private fun setUpClickListener() {
        binding.btnSaveAddress.setOnClickListener {
            if (saveAddress == null) {
                Toast.makeText(applicationContext, "Please select address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val data = Intent()
            data.putExtra(ADD_ADDRESS_KEY, saveAddress)
            data.putExtra(FLAG_KEY, flagIntent)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }
}