package com.yagnikkorat.devstreeandroidpractical

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.yagnikkorat.devstreeandroidpractical.databinding.ActivityMainBinding
import com.yagnikkorat.devstreeandroidpractical.viewmodels.MainViewModel

class MainActivity : AppCompatActivity(), AddressClickDeleteInterface, AddressClickEditInterface {

    companion object {
        const val UPDATE_ADDRESS_KEY = "updateAddress"
        const val SHARE_ADDRESS_KEY = "shareAddress"
        const val FLAG_KEY = "flag"
    }

    enum class FlagAddress {
        UPDATE, NORMAL, DELETE
    }

    lateinit var viewModal: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val addAddress = result.data?.getSerializableExtra(MapActivity.ADD_ADDRESS_KEY)
                val flagAddress = result.data?.getStringExtra(FLAG_KEY) ?: ""
                var distanse = 0.0


                if (addAddress is SaveAddressModel) {
                    if ((viewModal.address.value?.size ?: 0) > 0) {
                        distanse = SphericalUtil.computeDistanceBetween(
                            LatLng(
                                viewModal.address.value!![0].latitude!!,
                                viewModal.address.value!![0].longitude!!
                            ), LatLng(addAddress.latitude!!, addAddress.longitude!!)
                        )
                        distanse /= 1000
                    }
                    when (flagAddress) {
                        FlagAddress.UPDATE.name -> {
                            viewModal.updateAddress(
                                SaveAddressModel(
                                    id = addAddress.id,
                                    name = addAddress.name,
                                    address = addAddress.address,
                                    latitude = addAddress.latitude,
                                    longitude = addAddress.longitude,
                                    distance = distanse
                                )
                            )
                        }

                        FlagAddress.NORMAL.name -> {
                            viewModal.addAddress(
                                SaveAddressModel(
                                    name = addAddress.name,
                                    address = addAddress.address,
                                    latitude = addAddress.latitude,
                                    longitude = addAddress.longitude,
                                    distance = distanse
                                )
                            )
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setUpClickListener()


    }

    private fun setUpClickListener() = with(binding) {
        btnAddAddress.setOnClickListener {
            launchMap(null, FlagAddress.NORMAL.name)
        }

        btnASC.setOnClickListener {
            viewModal.rearrangeAddress(MainViewModel.AddressOrder.Ascending)
        }

        btnDESC.setOnClickListener {
            viewModal.rearrangeAddress(MainViewModel.AddressOrder.Descending)
        }

        btnRouting.setOnClickListener {
            val intent = Intent(this@MainActivity, RoutingActivity::class.java)
            val arr = ArrayList<SaveAddressModel>()
            viewModal.address.value?.let { arr.addAll(it) }
            Log.d("onMapReady", "onMapReady: ${arr.size}")
            intent.putExtra(SHARE_ADDRESS_KEY, arr)
            startActivity(intent)
        }
    }

    private fun initViewModel() {
        viewModal = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        val adapter = AddressAdapter(this, this)
        binding.rvAddress.adapter = adapter

        viewModal.address.observe(this, Observer {
            it?.let {
                adapter.updateList(it)
            }
        })
    }

    override fun onDeleteIconClick(address: SaveAddressModel) {
        viewModal.deleteAddress(address)
    }

    override fun onEditIconClick(address: SaveAddressModel) {
        launchMap(address, FlagAddress.UPDATE.name)
    }

    private fun launchMap(address: SaveAddressModel?, flagKey: String) {
        val intent = Intent(this, MapActivity::class.java)
        address?.let { intent.putExtra(UPDATE_ADDRESS_KEY, it) }
        intent.putExtra(FLAG_KEY, flagKey)
        intentLauncher.launch(intent)
    }
}
