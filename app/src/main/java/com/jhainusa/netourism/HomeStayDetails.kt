package com.jhainusa.netourism

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jhainusa.netourism.ui.theme.NETourismTheme
import com.jhainusa.netourism.ui.theme.back
import com.jhainusa.netourism.ui.theme.blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeStayDetailsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riverside Serenity",
                    fontFamily = poppinsFontFamily1) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle favorite */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "₹700/night", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                            fontFamily = poppinsFontFamily1)
                    }
                    Button(
                        onClick = { /* Handle booking */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue)
                    ) {
                        Text(text = "Book Now", color = Color.White,
                            fontFamily = poppinsFontFamily1)
                    }
                }
            }
        },
        containerColor = back
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ImageCarousel()
            Column(modifier = Modifier.padding(16.dp)) {
                TitleSection()
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                HostInfo()
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                AmenitiesSection()
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                LocationSection()
            }
        }
    }
}

@Composable
fun ImageCarousel() {
    val pagerState = rememberPagerState()
    val images = listOf(
        // For simplicity, I'm using a placeholder. You should use your actual images.
        R.drawable.place,
        R.drawable.boats,
        R.drawable.nohakili
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
        ) { page ->
            Image(
                painter = painterResource(images[page]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
        // Dots indicator can be added here
    }
}

@Composable
fun TitleSection() {
    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Lush Valley Homestay", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold,
            fontFamily = poppinsFontFamily1)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Meghalaya, India", style = MaterialTheme.typography.bodyLarge,
                fontFamily = poppinsFontFamily1,
                modifier = Modifier.weight(1f))
            Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
            Spacer(modifier = Modifier.width(4.dp))
            Text("4.9 (124 reviews)", style = MaterialTheme.typography.bodyMedium,
                fontFamily = poppinsFontFamily1)
        }
    }
}

@Composable
fun HostInfo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable._60_f_222851624_jfomgbjxwri5awgdpgxksabmnzcqo9rn), // Replace with host image
            contentDescription = "Host",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Hosted by Sarah", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                fontFamily = poppinsFontFamily1)
            Text("Khasi Tribe • Hosting for 5 years", style = MaterialTheme.typography.bodyMedium,
                fontFamily = poppinsFontFamily1)
        }
    }
}

@Composable
fun AmenitiesSection() {
    Column {
        Text("Amenities", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
            fontFamily = poppinsFontFamily1)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item { AmenityItem(icon = Icons.Default.Restaurant, text = "Local food") }
            item { AmenityItem(icon = Icons.Default.Hiking, text = "Guided trek") }
            item { AmenityItem(icon = Icons.Default.LocalFireDepartment, text = "Bonfire") }
        }
    }
}

@Composable
fun AmenityItem(icon: ImageVector, text: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = text)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text,
                fontFamily = poppinsFontFamily1)
        }
    }
}

@Composable
fun LocationSection() {
    Column {
        Text("Location", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
            fontFamily = poppinsFontFamily1)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray), // Placeholder for map
            contentAlignment = Alignment.Center
        ) {
            // You can integrate a map view here
            Image(
                painter = painterResource(id = R.drawable.mapimage), // Replace with map image
                contentDescription = "Map Location",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Button(
                onClick = { /* Open map */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View on map",
                    fontFamily = poppinsFontFamily1)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeStayDetailsScreenPreview() {
    NETourismTheme {
        HomeStayDetailsScreen()
    }
}
