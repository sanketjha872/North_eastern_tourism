package com.jhainusa.netourism

import com.jhainusa.netourism.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhainusa.netourism.ui.theme.blue

val manropeMedium = FontFamily(Font(R.font.manrope_medium))
data class ChartDataPoint(val x: Float, val y: Float)


@Preview
@Composable
fun AiSafetyScore() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(16.dp)
    ) {
        // Top Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AI Safety Status",
                        fontFamily = manropeMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Real-time monitoring engine",
                        fontFamily = manropeMedium,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.help_alt_svgrepo_com),
                    contentDescription = "Info",
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Score Section
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.LightGray,
                            style = Stroke(width = 8f)
                        )
                        drawArc(
                            color = Color(0xFFa8d7f4),
                            startAngle = -90f,
                            sweepAngle = (92 / 100f) * 360f,
                            useCenter = false,
                            style = Stroke(width = 8f)
                        )
                    }
                    Text(
                        text = "92",
                        fontFamily = manropeMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Low Risk",
                        fontFamily = manropeMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "92/100",
                        fontFamily = manropeMedium,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }


        // AI Insights Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "AI Insights",
                    fontFamily = manropeMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                InsightRow(
                    icon = R.drawable.info_svgrepo_com,
                    text = "No unusual activity detected in last 2 hours."
                )
                InsightRow(
                    icon = R.drawable.copy_svgrepo_com,
                    text = "Movement stable and consistent with itinerary."
                )
                InsightRow(icon = R.drawable.location_pin_svgrepo_com, text = "Current area rated safe.")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Activity Timeline Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Activity Timeline",
                    fontFamily = manropeMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActivityTimelineChart()
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Recent AI Alerts Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Recent AI Alerts",
                    fontFamily = manropeMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                AlertRow(
                    icon = R.drawable.danger_triangle_svgrepo_com,
                    title = "Inactivity Detected",
                    time = "35 minutes ago"
                )
                AlertRow(
                    icon = R.drawable.location_pin_alt_1_svgrepo_com,
                    title = "Minor Route Deviation",
                    time = "1 hour ago"
                )
            }
        }
    }
}

@Composable
fun InsightRow(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = blue
        )
        Spacer(modifier = Modifier.padding(start = 16.dp))
        Text(text = text, fontFamily = manropeMedium, fontSize = 14.sp)
    }
}

@Composable
fun AlertRow(icon: Int, title: String, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = blue
        )
        Spacer(modifier = Modifier.padding(start = 16.dp))
        Column {
            Text(text = title, fontFamily = manropeMedium, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = time, fontFamily = manropeMedium, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun ActivityTimelineChart() {
    val data = listOf(
        ChartDataPoint(0f, 30f),
        ChartDataPoint(1.5f, 35f),
        ChartDataPoint(3f, 50f),
        ChartDataPoint(4.5f, 55f),
        ChartDataPoint(6f, 68f),
        ChartDataPoint(7.5f, 60f),
        ChartDataPoint(9f, 55f),
        ChartDataPoint(11f, 40f)
    )

    val yLabels = listOf("70", "60", "50", "40", "30")
    val xLabels = listOf("12AM", "3AM", "6AM", "11AM")

    val yMin = 20f
    val yMax = 80f
    val xMin = 0f
    val xMax = 12f

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                yLabels.forEach {
                    Text(text = it, fontFamily = manropeMedium, fontSize = 10.sp, color = Color.Gray)
                }
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path()
                val xScale = size.width / (xMax - xMin)
                val yScale = size.height / (yMax - yMin)

                // Draw grid lines
                val gridPathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                (30..70 step 10).forEach { yVal ->
                    val y = size.height - ((yVal - yMin) * yScale)
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f,
                        pathEffect = gridPathEffect
                    )
                }

                // Draw path
                data.forEachIndexed { index, point ->
                    val x = point.x * xScale
                    val y = size.height - ((point.y - yMin) * yScale)
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                drawPath(
                    path = path,
                    color = Color(0xFF0077B6),
                    style = Stroke(width = 4f)
                )

                // Draw points
                data.forEach { point ->
                    val x = point.x * xScale
                    val y = size.height - ((point.y - yMin) * yScale)
                    drawCircle(
                        color = Color.White,
                        radius = 8f,
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = Color(0xFF0077B6),
                        radius = 6f,
                        center = Offset(x, y)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 10.dp), // Adjust padding to align
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            xLabels.forEach {
                Text(text = it, fontFamily = manropeMedium, fontSize = 10.sp, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("Low", fontFamily = manropeMedium, fontSize = 12.sp, color = Color.Gray)
            Text("Moderate", fontFamily = manropeMedium, fontSize = 12.sp, color = Color.Gray)
            Text("High", fontFamily = manropeMedium, fontSize = 12.sp, color = Color.Gray)
        }
    }
}