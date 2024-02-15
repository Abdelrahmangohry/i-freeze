package com.lock.data.repo.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.lock.data.model.LocationDataAddress
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepository(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun fetchLocation(): LocationDataAddress {
        return if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationDataAddress(null, "Location permission not granted")
        } else {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                try {
                    val locationState = getLastLocation()
                    if (locationState != null) {
                        val geoCoder = Geocoder(context)

                        val currentLocation = geoCoder.getFromLocation(locationState.latitude, locationState.longitude, 1)
                        LocationDataAddress(currentLocation?.first()?.getAddressLine(0), null)
                    } else {
                        LocationDataAddress(null, "Location is null")
                    }
                } catch (e: Exception) {
                    LocationDataAddress(null, "Failed to fetch location: ${e.message}")
                }
            } else {
                LocationDataAddress(null, "Location services are not enabled")
            }
        }
    }

    private suspend fun getLastLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Handle the case where permissions are not granted.
            }
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
}