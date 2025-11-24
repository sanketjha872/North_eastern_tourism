package com.jhainusa.netourism

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jhainusa.netourism.ui.theme.NETourismTheme

// Assuming poppinsmedium.ttf is in res/font
val poppinsJourneyTimeline = FontFamily(
    Font(R.font.manrope_medium)
)

data class TimelineEvent(
    val id: String,
    val title: String,
    val address: String,
    val time: String,
    val isLast: Boolean = false
)

// Sample Data - Replace with your actual data and drawables
val sampleTimelineEvents = listOf(
    TimelineEvent("1", "Journey Started", "123 Main St, Anytown", "10:00 AM"),
    TimelineEvent("2", "Location Update", "Oak Ave & Park Rd", "10:15 AM"),
    TimelineEvent("3", "Destination Reached", "456 Central Plaza", "10:30 AM", isLast = true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyTimelineScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Journey Timeline", 
                        fontFamily = poppinsJourneyTimeline, 
                        fontWeight = FontWeight.SemiBold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)) // Light gray background for the area behind the bottom sheet
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.mapimage), // Replace with your map image
                    contentDescription = "Map view of journey",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Map Control FABs
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloatingActionButton(
                        onClick = { /* TODO: Zoom In */ },
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                        containerColor = Color(0xFF2C2C2E), // Dark FAB background
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Zoom In")
                    }
                    FloatingActionButton(
                        onClick = { /* TODO: Zoom Out */ },
                        shape = RoundedCornerShape(0.dp),
                        containerColor = Color(0xFF2C2C2E),
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Clear, contentDescription = "Zoom Out")
                    }
                    FloatingActionButton(
                        onClick = { /* TODO: Recenter Map */ },
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
                        containerColor = Color(0xFF2C2C2E),
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Recenter") // Or MyLocation
                    }
                }
            }

            // Timeline Bottom Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF1C1C1E), // Dark background for timeline section
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(top = 20.dp) // Padding for inside the dark section
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Timeline", 
                        color = Color.White, 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsJourneyTimeline
                    )
                    Text(
                        "Tracking Active", 
                        color = Color.LightGray, 
                        fontSize = 12.sp,
                        fontFamily = poppinsJourneyTimeline
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .heightIn(max = 200.dp) // Max height before scrolling, adjust as needed
                ) {
                    items(sampleTimelineEvents) { event ->
                        TimelineItem(event = event)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp)) // Space before the button

                Button(
                    onClick = { /* TODO: End Journey & Share Report action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)) // Blue button color
                ) {
                    Text(
                        "End Journey & Share Report", 
                        color = Color.White, 
                        fontFamily = poppinsJourneyTimeline, 
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineItem(event: TimelineEvent) {
    Row(modifier = Modifier.padding(bottom = 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF007AFF)) // Blue dot
            )
            if (!event.isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp) // Height of the line, adjust as needed
                        .background(Color(0xFF007AFF).copy(alpha = 0.5f))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                event.title, 
                color = Color.White, 
                fontWeight = FontWeight.SemiBold, 
                fontSize = 16.sp,
                fontFamily = poppinsJourneyTimeline
            )
            Text(
                event.address, 
                color = Color.LightGray, 
                fontSize = 14.sp,
                fontFamily = poppinsJourneyTimeline
            )
            Text(
                event.time, 
                color = Color.Gray, 
                fontSize = 12.sp,
                fontFamily = poppinsJourneyTimeline
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_6_pro")
@Composable
fun JourneyTimelineScreenPreview() {
    NETourismTheme(darkTheme = true) { // Use darkTheme true for better preview matching image
        JourneyTimelineScreen(navController = rememberNavController())
    }
}

// Note: Replace R.drawable.journey_map_placeholder with an actual map image in your res/drawable folder.
// Ensure poppinsmedium.ttf is in res/font.
