package com.jhainusa.netourism.Map

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
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
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun NavigationScreen() {
    val context = LocalContext.current
    var startText by remember { mutableStateOf("") }
    var endText by remember { mutableStateOf("") }
    var mapView: MapView? by remember { mutableStateOf(null) }

    Box(Modifier.fillMaxSize()) {
        // OSMDroid Map
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

        // UI overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            OutlinedTextField(
                value = startText,
                onValueChange = { startText = it },
                placeholder = { Text("Start Destination", fontFamily = poppinsFontFamily1) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                shape = RoundedCornerShape(20.dp),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = poppinsFontFamily1,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White, // White background
                    unfocusedContainerColor = Color.White // White background
                )
            )
            Spacer(modifier = Modifier.height(7.dp))
            OutlinedTextField(
                value = endText,
                onValueChange = { endText = it },
                placeholder = { Text("Destination", fontFamily = poppinsFontFamily1) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                shape = RoundedCornerShape(20.dp),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = poppinsFontFamily1,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White, // White background
                    unfocusedContainerColor = Color.White // White background
                )
            )

            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = {
                    Log.d("ROUTE_DEBUG", "Button clicked! mapView=$mapView")
                    fetchRoute(startText, endText, mapView)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = blue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Start Navigation",
                    fontFamily = poppinsFontFamily1,
                    color = Color.White
                    )
            }
        }
    }
}

fun fetchRoute(start: String, end: String, map: MapView?) {
    if (map == null) return

    CoroutineScope(Dispatchers.IO).launch {
        try {
            // --- GEOCODING ---
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

            Log.d("ROUTE_DEBUG", "Start=$startPoint End=$endPoint")

            // --- GRAPH HOPPER ROUTE ---
            val route = try {
                ApiClient.graphhopper.getRoute(
                    start = startPoint,
                    end = endPoint,
                    pointsEncoded = false,
                    key = "42d6c614-7479-4b21-b794-0c2de68ac429"  // replace with your GraphHopper API key
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

            // --- DRAW POLYLINE ---
            val coords = route.paths[0].points.coordinates
            val polyline = Polyline().apply {
                outlinePaint.color = color
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

