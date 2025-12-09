package com.jhainusa.netourism.Map

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.jhainusa.netourism.R
import com.jhainusa.netourism.SupaBase.loadZones
import com.jhainusa.netourism.Zones.Zone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationService : LifecycleService() {

    private lateinit var fused: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private var zones = mutableListOf<Zone>()
    private val lastNotificationTimestamps = mutableMapOf<String, Long>()
    private var lastLocation: Location? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "onCreate")
        fused = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
        fetchZones()
        startLocationUpdates()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun fetchZones() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val loadedZones = loadZones()
                zones.clear()
                zones.addAll(loadedZones)
                Log.d("LocationService", "Fetched zones: ${zones.size}")
            } catch (e: Exception) {
                Log.e("LocationService", "Error fetching zones", e)
            }
        }
    }

    private fun startLocationUpdates() {
        Log.d("LocationService", "startLocationUpdates called")
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L // every 10 seconds
        ).build()

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LocationService", "Missing location permission! Cannot start updates.")
            stopSelf()
            return
        }

        Log.d("LocationService", "Location permission is granted. Requesting updates.")
        fused.requestLocationUpdates(request, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                Log.d("LocationService", "onLocationResult received")
                val loc = result.lastLocation
                if (loc != null) {
                    if (lastLocation == null || loc.distanceTo(lastLocation!!) > 5f) {
                        Log.d("LocationService", "Location update: ${loc.latitude}, ${loc.longitude}")
                        LocationRepository.updateLocation(loc)
                        checkProximityToZones(loc)
                        lifecycleScope.launch {
                            try {
                                val addresses = withContext(Dispatchers.IO) {
                                    geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                                }
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
                        }
                        lastLocation = loc
                    } else {
                        Log.d("LocationService", "Insignificant location change.")
                    }
                } else {
                    Log.d("LocationService", "Location update is null")
                }
            }
        }, Looper.getMainLooper())
    }

    private fun checkProximityToZones(location: Location) {
        for (zone in zones) {
            val zoneLocation = Location("").apply {
                latitude = zone.latitude ?: 0.0
                longitude = zone.longitude ?: 0.0
            }
            val distance = location.distanceTo(zoneLocation)
            if (distance < (zone.radius_meters ?: 100.0)) {
                zone.name?.let {
                    val currentTime = System.currentTimeMillis()
                    val lastNotificationTime = lastNotificationTimestamps[it] ?: 0L
                    if (currentTime - lastNotificationTime > 10000) { // 10 seconds
                        sendProximityNotification(zone, distance)
                        lastNotificationTimestamps[it] = currentTime
                    }
                }
            }
        }
    }

    private fun sendProximityNotification(zone: Zone, distance: Float) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "proximity_notification_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Proximity Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("You're near a zone!")
            .setContentText("You are within ${distance.toInt()}m of ${zone.name} and your safety score is 70")
            .setSmallIcon(R.drawable.ne_safe)

            // 🔥 THIS IS THE IMPORTANT FIX
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("You are within ${distance.toInt()}m of ${zone.name} and your safety score is 70")
            )

            .build()


        notificationManager.notify(PROXIMITY_NOTIFICATION_ID, notification)
    }


    private fun createNotification(): Notification {
        val channelId = "location_service_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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
        private const val PROXIMITY_NOTIFICATION_ID = 2
    }
}