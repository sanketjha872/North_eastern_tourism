package com.jhainusa.netourism.UserPreferences

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import com.jhainusa.netourism.Map.LocationRepository
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@Preview
@Composable
fun LocationInMap() {
    val location by LocationRepository.lastKnownLocation.collectAsState()
    val currentLocation by LocationRepository.currentPlaceName.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        OsmMapScreen()
        Column {
            if (location != null) {
                Text(text = "Latitude: ${location?.latitude}")
                Text(text = "Longitude: ${location?.longitude}")
                Text(text = "CurrentLocation : ${currentLocation}")
            } else {
                Text(text = "Waiting for location...")
            }
        }
    }
}

@Composable
fun OsmMapScreen() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->

            // Load config
            Configuration.getInstance().load(
                context,
                PreferenceManager.getDefaultSharedPreferences(context)
            )

            val map = MapView(context).apply {
                setMultiTouchControls(true)
                controller.setZoom(18.0)
            }

            // 🔵 Show current location
            val locationOverlay = MyLocationNewOverlay(
                GpsMyLocationProvider(context),
                map
            )
            locationOverlay.enableMyLocation()
            locationOverlay.enableFollowLocation()  // map follows your movement

            map.overlays.add(locationOverlay)

            map
        }
    )

}
