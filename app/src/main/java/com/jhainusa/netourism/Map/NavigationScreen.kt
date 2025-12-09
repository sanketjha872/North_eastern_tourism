package com.jhainusa.netourism.Map

import android.util.Log
import android.widget.Toast
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
import com.jhainusa.netourism.Zones.Zone
import com.jhainusa.netourism.poppinsFontFamily1
import com.jhainusa.netourism.ui.theme.blue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

data class ZoneShape(
    val points: List<GeoPoint>,
    val color: Int
)

fun Zone.getMapPointsWithColor(): ZoneShape {
    val polygonPoints = if (latitude != null && longitude != null) {
        Polygon.pointsAsCircle(GeoPoint(latitude, longitude), radius_meters ?: 100.0)
    } else {
        geom?.coordinates?.firstOrNull()?.map { GeoPoint(it[1], it[0]) } ?: emptyList()
    }

    // Pick color based on zone type, radius, or just randomly
    val fillColor = when (risk_level) {
        "LOW" -> android.graphics.Color.argb(100, 255, 0, 0)  // red
        "MEDIUM" -> android.graphics.Color.argb(180, 220, 20, 60)
        else -> android.graphics.Color.argb(255,139, 0, 0)      // blue
    }

    return ZoneShape(polygonPoints, fillColor)
}

@Composable
fun NavigationScreen(navController: NavController, viewModel: ReportViewModel) {
    val zones by viewModel.zones.collectAsState(initial = emptyList())
    val context = LocalContext.current
    var startText by remember { mutableStateOf("") }
    var endText by remember { mutableStateOf("") }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchZones() }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(18.0)
                    tileProvider.tileSource = TileSourceFactory.MAPNIK

                    val rotationOverlay = org.osmdroid.views.overlay.gestures.RotationGestureOverlay(this)
                    rotationOverlay.isEnabled = true
                    overlays.add(rotationOverlay)

                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    locationOverlay.enableMyLocation()
                    locationOverlay.enableFollowLocation()
                    overlays.add(locationOverlay)
                    mapView = this // Capture the MapView instance
                }
            },
            update = { map ->
                map.overlays.removeAll { it is Polygon || it is Marker }

                zones.forEach { zone ->
                    val shape = zone.getMapPointsWithColor()
                    if (shape.points.isNotEmpty()) {
                        val polygon = Polygon().apply {
                            points = shape.points
                            fillColor = shape.color
                            strokeColor = shape.color
                            strokeWidth = 4f
                        }
                        map.overlays.add(polygon)

                        val marker = Marker(map).apply {
                            position = shape.points.first()
                            icon = context.getDrawable(R.drawable.location_pin_svgrepo_com)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        map.overlays.add(marker)
                    }
                }

                val centerPoint = zones.firstOrNull()?.getMapPointsWithColor()?.points?.firstOrNull()
                    ?: GeoPoint(28.6008, 77.3503)
                map.controller.setCenter(centerPoint)
                map.invalidate()
            }
        )
        // This Column holds all the controls at the top.
        // It will only take up the space it needs.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Already on this screen */ },
                    colors = ButtonDefaults.buttonColors(containerColor = blue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Safety Zones", color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { navController.navigate(MapScreen.NearbyTourists.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Nearby Tourists", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = startText,
                onValueChange = { startText = it },
                placeholder = { Text("Start Destination", fontFamily = poppinsFontFamily1) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                textStyle = TextStyle(fontSize = 16.sp, fontFamily = poppinsFontFamily1),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(7.dp))
            OutlinedTextField(
                value = endText,
                onValueChange = { endText = it },
                placeholder = { Text("Destination", fontFamily = poppinsFontFamily1) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                textStyle = TextStyle(fontSize = 16.sp, fontFamily = poppinsFontFamily1),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = { fetchRoute(startText, endText, mapView) },
                colors = ButtonDefaults.buttonColors(containerColor = blue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Start Navigation", fontFamily = poppinsFontFamily1, color = Color.White)
            }
        }

        // This AndroidView holds the map. 
        // The weight modifier makes it fill all the remaining space in the parent Column.

    }
}

fun fetchRoute(start: String, end: String, map: MapView?) {
    if (map == null) {
        Log.e("ROUTE_DEBUG", "MapView is null, cannot fetch route.")
        return
    }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val startResults = ApiClient.nominatim.search(start)
            val endResults = ApiClient.nominatim.search(end)

            if (startResults.isEmpty() || endResults.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(map.context, "Location not found", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val startGeo = startResults.first()
            val endGeo = endResults.first()

            val startPoint = "${startGeo.lat},${startGeo.lon}"
            val endPoint = "${endGeo.lat},${endGeo.lon}"

            val route = try {
                ApiClient.graphhopper.getRoute(
                    start = startPoint,
                    end = endPoint,
                    pointsEncoded = false,
                    key = "42d6c614-7479-4b21-b794-0c2de68ac429"
                )
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 403) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(map.context, "API key invalid or limit reached", Toast.LENGTH_LONG).show()
                    }
                }
                return@launch
            }

            if (route.paths.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(map.context, "No route found", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val coords = route.paths[0].points.coordinates
            val polyline = Polyline().apply {
                outlinePaint.color = android.graphics.Color.BLUE
                outlinePaint.strokeWidth = 12f
                coords.forEach { addPoint(GeoPoint(it[1], it[0])) }
            }

            withContext(Dispatchers.Main) {
                map.overlays.clear()
                map.overlays.add(polyline)
                map.controller.setCenter(GeoPoint(coords[0][1], coords[0][0]))
                map.controller.setZoom(10.0)
                map.invalidate()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(map.context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
