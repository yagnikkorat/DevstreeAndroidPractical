package com.yagnikkorat.devstreeandroidpractical

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class RoutingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val apiKey = "AIzaSyBSNyp6GQnnKlrMr7hD2HGiyF365tFlK5U"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routing)

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        // Map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }
    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        val addAddr =
            intent?.getSerializableExtra(MainActivity.SHARE_ADDRESS_KEY) as ArrayList<SaveAddressModel>?
        Log.d("onMapReady", "onMapReady: ${addAddr}")
        if (addAddr is ArrayList<SaveAddressModel>) {
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        addAddr[0].latitude!!,
                        addAddr[0].longitude!!
                    )
                )
            )
            for (i in 1 until addAddr.size) {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            addAddr[i].latitude!!,
                            addAddr[i].longitude!!
                        )
                    )
                )
                val urll = getDirectionURL(
                    LatLng(addAddr[i - 1].latitude!!, addAddr[i - 1].longitude!!),
                    LatLng(addAddr[i].latitude!!, addAddr[i].longitude!!),
                    apiKey
                )
                getDirection(urll)
            }
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        addAddr[0].latitude!!,
                        addAddr[0].longitude!!
                    ), 14F
                )
            )
        }
    }

    private fun getDirection(url: String) {
        lifecycleScope.launch {

            val result = async(Dispatchers.IO) {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                val data = response.body!!.string()

                val result = ArrayList<List<LatLng>>()
                try {
                    val respObj = Gson().fromJson(data, MapData::class.java)
                    val path = ArrayList<LatLng>()
                    for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                        path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                    }
                    result.add(path)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@async result
            }

            val finalResult = result.await()
            if (finalResult.size > 0) {
                withContext(Dispatchers.Main) {
                    val lineoption = PolylineOptions()
                    for (i in finalResult.indices) {
                        lineoption.addAll(finalResult[i])
                        lineoption.width(10f)
                        lineoption.color(Color.GREEN)
                        lineoption.geodesic(true)
                    }
                    mMap.addPolyline(lineoption)
                }
            }
        }
    }
}
