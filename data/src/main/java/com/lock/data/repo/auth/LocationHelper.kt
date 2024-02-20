package com.lock.data.repo.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lock.data.model.LocationDataAddress
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale


object LocationHelper {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private const val permissionId = 2

    interface LocationCallback {
        suspend fun onLocationFetched(locationData: LocationDataAddress)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    fun getLocation(context: Context, callback: LocationCallback) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (checkPermissions(context)) {
            if (isLocationEnabled(context)) {
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location: Location? = task.result
                        val latitude = location?.latitude
                        val longitude = location?.longitude
                        if (latitude != null && longitude != null) {
                            val geocoder = Geocoder(context, Locale.ENGLISH)
                            try {
                                val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                                if (addresses?.isNotEmpty() == true) {
                                    val address = addresses[0]
                                    val addressLine = address.getAddressLine(0)
                                    val locationData = LocationDataAddress(addressLine, latitude, longitude)
                                    GlobalScope.launch {
                                        callback.onLocationFetched(locationData)
                                    }
                                } else {
                                    Log.e("abdo", "No address found")
                                }
                            } catch (e: Exception) {
                                Log.e("abdo", "Error getting address: ${e.message}")
                            }
                        } else {
                            Log.e("abdo", "Latitude or Longitude is null")
                        }
                    } else {
                        Log.e("abdo", "Location task failed or result is null")
                        // Handle case when location is not available
                    }
                }
            } else {
                // Location services are disabled, prompt the user to enable them
                showLocationDisabledMessage(context)
            }
        } else {
            // Request location permissions
            requestPermissions(context)
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(context: Context) {
        ActivityCompat.requestPermissions(
            context as AppCompatActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun showLocationDisabledMessage(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }
}