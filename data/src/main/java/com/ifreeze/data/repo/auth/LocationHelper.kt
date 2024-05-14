package com.ifreeze.data.repo.auth

import android.Manifest
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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ifreeze.data.model.LocationDataAddress
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale


object LocationHelper {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private const val permissionId = 2

    interface LocationCallback {
        suspend fun onLocationFetched(locationData: LocationDataAddress)
    }


    fun getLocation(context: Context, callback: LocationCallback) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (checkPermissions(context)) {
            if (isLocationEnabled(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //requestPermissions(context)
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val location: Location? = task.result
                        val latitude = location?.latitude
                        val longitude = location?.longitude
                        if (latitude != null && longitude != null) {
                            val geocoder = Geocoder(context, Locale.ENGLISH)
                            try {
                                val addresses: MutableList<Address>? =
                                    geocoder.getFromLocation(latitude, longitude, 1)
                                if (addresses?.isNotEmpty() == true) {
                                    val address = addresses[0]
                                    val addressLine = address.getAddressLine(0)
                                    val locationData =
                                        LocationDataAddress(addressLine, latitude, longitude)
                                    GlobalScope.launch {
                                        callback.onLocationFetched(locationData)
                                    }
                                } else {
                                    Log.e("abdo", "No address found")
                                }
                            } catch (e: Exception) {

                                Log.e("abdo", "Error during data syncing: ${e.message}", e)
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
                Log.d("abdo", "getLocation: error")
                // TODO: add message overdraw
                // Toast.makeText(context,"enable gps", Toast.LENGTH_LONG).show()
            }
        } else {
//            Toast.makeText(context, "Please Give I-Freeze The Location Permission", Toast.LENGTH_SHORT).show()
            // Request location permissions
            createLocationRequest(context as AppCompatActivity)
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



    private fun createLocationRequest(activity: AppCompatActivity) {
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    private fun showLocationDisabledMessage(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }
}