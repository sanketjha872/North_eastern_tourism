package com.jhainusa.netourism.Map

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.jhainusa.netourism.R
import java.util.Locale

class LocationService : LifecycleService() {

    private lateinit var fused: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "onCreate")
        fused = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        startLocationUpdates()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun startLocationUpdates() {
        Log.d("LocationService", "startLocationUpdates")
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 3000L // every 3 seconds
        ).build()

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LocationService", "Missing location permission")
            return
        }

        fused.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation
                if (loc != null) {
                    Log.d("LocationService", "Location update: ${loc.latitude}, ${loc.longitude}")
                    LocationRepository.updateLocation(loc)
                    try {
                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val fullAddress = address.getAddressLine(0) // This gets the full address
                            fullAddress?.let {
                                Log.d("LocationService", "Place name: $it")
                                LocationRepository.updatePlaceName(it)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("LocationService", "Error getting address from location", e)
                    }
                } else {
                    Log.d("LocationService", "Location update is null")
                }
            }
        }, Looper.getMainLooper())
    }

    private fun createNotification(): Notification {
        val channelId = "location_service_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Tracking your location in the background")
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app's icon
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationService", "onDestroy")
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}