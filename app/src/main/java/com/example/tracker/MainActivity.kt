package com.example.tracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

const val LOCATION_REQUEST = 100
const val LOCATION_PERMISSION_REQUEST = 101

class MainActivity : AppCompatActivity() {

    private lateinit var locViewModel: LocViewModel
    private var isGPSEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locViewModel = ViewModelProviders.of(this).get(LocViewModel::class.java)

        LocUtil(this).turnGPSOn(object : LocUtil.OnLocationOnListener {
            override fun locationStatus(isLocationOn: Boolean) {
                this@MainActivity.isGPSEnabled = isLocationOn
            }
        })
    }


    private fun observeLocationUpdates() {
        locViewModel.getLocationData.observe(this, Observer {
            longitude.text = it.longitude.toString()
            latitude.text = it.latitude.toString()
        })
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        when {
            isPermissionsGranted() -> {
                observeLocationUpdates()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_REQUEST
                )
            }
        }
    }


    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun isPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION ) && ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_COARSE_LOCATION)


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                startLocationUpdates()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LOCATION_PERMISSION_REQUEST) {
                isGPSEnabled = true
                startLocationUpdates()
            }
        }
    }
}