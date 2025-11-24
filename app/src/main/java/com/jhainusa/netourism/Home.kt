package com.jhainusa.netourism

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place // Placeholder for Places
import androidx.compose.material.icons.filled.ThumbUp // Placeholder for Tips
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource // Required for painterResource
import androidx.compose.ui.text.font.Font // Added import
import androidx.compose.ui.text.font.FontFamily // Added import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhainusa.netourism.ui.theme.NETourismTheme
import kotlinx.coroutines.launch

// Define Poppins font family (assuming poppinsmedium.ttf in res/font)
val poppinsFontFamily = FontFamily(
    Font(R.font.manrope_medium)
)

// Data class for a Place
data class Place(
    val name: String,
    @DrawableRes val imageResId: Int, // Use @DrawableRes for image resources
    val description: String
)

// Sample list of places (replace with your actual data and drawables)
val samplePlaces = listOf(
    Place("Tawang Monastery", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash, "Largest monastery in India."),
    Place("Kaziranga National Park", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash, "Home of the one-horned rhinoceros."),
    Place("Majuli Island", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash, "Largest river island in the world."),
    Place("Cherrapunji", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash, "Known for its living root bridges."),
    Place("Nohkalikai Falls", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash, "Tallest plunge waterfall in India.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent()
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Home") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) 
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SafetyMapSection(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Popular Places to Visit",
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = poppinsFontFamily1, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(samplePlaces) { place ->
                            PlaceCard(place = place)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                ActionCards(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun SafetyMapSection(modifier: Modifier = Modifier) { // Added modifier parameter
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier // Applied modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.screenshot_2025_09_11_013925),
                contentDescription = "Map Placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "Safety Score",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = poppinsFontFamily1,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            SafetyPin(score = "7", alignment = Alignment.CenterStart, modifier = Modifier.offset(x = 50.dp, y = (-20).dp), color = Color(0xFF4CAF50))
            SafetyPin(score = "8", alignment = Alignment.TopEnd, modifier = Modifier.offset(x = (-80).dp, y = 40.dp), color = Color(0xFF4CAF50))
            SafetyPin(score = "6", alignment = Alignment.CenterStart, modifier = Modifier.offset(x = 80.dp, y = 70.dp), color = Color(0xFFFFEB3B))
            SafetyPin(score = "4", alignment = Alignment.CenterEnd, modifier = Modifier.offset(x = (-60).dp, y = 20.dp), color = Color(0xFFFF9800))
        }
    }
}

@Composable
fun SafetyPin(score: String, alignment: Alignment, modifier: Modifier = Modifier, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
            .wrapContentSize(align = alignment)
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
    ) {
        Text(
            text = score,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            fontFamily = poppinsFontFamily1
        )
    }
}

@Composable
fun ActionCards(modifier: Modifier = Modifier) { // Added modifier parameter
    Row(
        modifier = modifier // Applied modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ActionCard(title = "Tips", icon = Icons.Filled.ThumbUp, onClick = { /* TODO */ })
        ActionCard(title = "Places", icon = Icons.Filled.Place, onClick = { /* TODO */ })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(150.dp)
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                fontFamily = poppinsFontFamily1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceCard(place: Place) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(220.dp), // Increased height for description
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = place.imageResId), // Use actual image resource
                contentDescription = place.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = place.name,
                    fontFamily = poppinsFontFamily1,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = place.description,
                    fontFamily = poppinsFontFamily1,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    NETourismTheme {
        HomeScreen()
    }
}

// Note: For the samplePlaces to display images correctly, you need to replace
// R.drawable.map_placeholder with actual drawable resources in your res/drawable folder.
