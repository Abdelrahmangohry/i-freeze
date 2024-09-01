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


/**
 * Helper object for handling location-related tasks such as fetching the user's current location
 * and checking necessary permissions. This object provides utility functions to access
 * location data and manage location permissions within an Android application.
 */
object LocationHelper {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private const val permissionId = 2

    /**
     * Interface to be implemented by the caller to receive the location data once it is fetched.
     */
    interface LocationCallback {

        /**
         * Called when the location data is successfully fetched.
         *
         * @param locationData The fetched location data including address, latitude, and longitude.
         */
        suspend fun onLocationFetched(locationData: LocationDataAddress)
    }

    /**
     * Fetches the user's current location and provides the data through the callback.
     * Checks for location permissions and whether location services are enabled before attempting to fetch the location.
     *
     * @param context The context used to access system services and resources.
     * @param callback The callback to be invoked when the location is successfully fetched.
     */
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
                // add message overdraw
            }
        } else {
            createLocationRequest(context as AppCompatActivity)
        }
    }

    /**
     * Checks whether location services (GPS or Network) are enabled on the device.
     *
     * @param context The context used to access system services.
     * @return True if location services are enabled, false otherwise.
     */
    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /**
     * Checks whether the necessary location permissions (ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION)
     * are granted to the application.
     *
     * @param context The context used to check the permissions.
     * @return True if both permissions are granted, false otherwise.
     */
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


    /**
     * Requests location permissions from the user if they have not already been granted.
     * This method should be called if permissions are needed and the user has not granted them yet.
     *
     * @param activity The activity context used to request permissions.
     */
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

    /**
     * Displays a message prompting the user to enable location services.
     * Redirects the user to the location settings page.
     *
     * @param context The context used to start the settings activity.
     */
    private fun showLocationDisabledMessage(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }
}