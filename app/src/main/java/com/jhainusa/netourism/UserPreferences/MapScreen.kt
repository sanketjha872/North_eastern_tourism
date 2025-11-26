package com.jhainusa.netourism.UserPreferences

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


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

