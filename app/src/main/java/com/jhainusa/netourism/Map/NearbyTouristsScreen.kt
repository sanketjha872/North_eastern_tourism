package com.jhainusa.netourism.Map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import com.jhainusa.netourism.MapScreen
import com.jhainusa.netourism.R
import com.jhainusa.netourism.SupaBase.ReportViewModel
import com.jhainusa.netourism.poppinsFontFamily1
import com.jhainusa.netourism.ui.theme.blue
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun NearbyTouristsScreen(navController: NavController, viewModel: ReportViewModel) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val nearbyTourists by viewModel.nearbyTourists.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchNearbyTourists()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                Configuration.getInstance().load(it, PreferenceManager.getDefaultSharedPreferences(it))
                MapView(it).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(18.0)
                    tileProvider.tileSource = TileSourceFactory.MAPNIK

                    val rotationOverlay = org.osmdroid.views.overlay.gestures.RotationGestureOverlay(this)
                    rotationOverlay.isEnabled = true
                    overlays.add(rotationOverlay)

                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(it), this)
                    locationOverlay.enableMyLocation()
                    locationOverlay.enableFollowLocation()
                    overlays.add(locationOverlay)
                }
            },
            update = { map ->
                map.overlays.removeAll { it is Marker }

                nearbyTourists.forEach { tourist ->
                    val marker = Marker(map).apply {
                        position = GeoPoint(tourist.latitude, tourist.longitude)
                        icon = context.getDrawable(R.drawable.person_svgrepo_com)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    map.overlays.add(marker)
                }

                val centerPoint = nearbyTourists.firstOrNull()?.let { GeoPoint(it.latitude, it.longitude) }
                    ?: map.mapCenter as GeoPoint
                map.controller.setCenter(centerPoint)
                map.invalidate()
            }
        )
        // UI elements are in a column at the top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate(MapScreen.SafetyZones.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Safety Zones", color = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* Already on this screen */ },
                    colors = ButtonDefaults.buttonColors(containerColor = blue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Nearby Tourists", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search for a location...", fontFamily = poppinsFontFamily1) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = poppinsFontFamily1,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        // The map fills the rest of the screen

    }
}
