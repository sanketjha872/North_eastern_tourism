package com.jhainusa.netourism

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jhainusa.netourism.ui.theme.NETourismTheme
import kotlinx.coroutines.launch

val poppinsFontFamily1 = FontFamily(
    Font(R.font.manrope_medium)
)

data class ActivityItemData(
    val name: String,
    @DrawableRes val imageResId: Int // Placeholder image resource
)

data class RecommendedPlaceData(
    val name: String,
    @DrawableRes val imageResId: Int, // Placeholder image resource
    val location: String,
    val travellers: Int,
    var isFavorite: Boolean
)

// Sample Data (Replace R.drawable.placeholder_image and R.drawable.activity_placeholder with actual drawables)
val sampleActivities = listOf(
    ActivityItemData("Hiking", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash),
    ActivityItemData("Biking", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash),
    ActivityItemData("Climbing", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash),
    ActivityItemData("Running", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash),
    ActivityItemData("Jumping", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash)
)

val sampleRecommendedPlaces = listOf(
    RecommendedPlaceData("Hill Climbing", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash, "San Francisco", 7, false),
    RecommendedPlaceData("Mountain Trek", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash, "San Francisco", 12, true),
    RecommendedPlaceData("Forest Trail", R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash, "San Francisco", 5, false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FirstPageScreen(mainNav : NavController = rememberNavController()) {
    var searchQuery by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Midtown, New York",
                                    fontFamily = poppinsFontFamily1,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Safety Level: Low Risk",
                                    fontFamily = poppinsFontFamily1,
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {mainNav.navigate("DrawerContent")}) {
                            Icon(Icons.Filled.Notifications, contentDescription = "Notifications", modifier = Modifier.size(28.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color(0xFFE0F7FA) // Make scaffold background transparent
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Light blue gradient-like background for the top portion
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0F7FA).copy(alpha = 0.45f)) // Light cyan, adjust color
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // Apply padding from scaffold
                        .padding(horizontal = 14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search your place", fontFamily = poppinsFontFamily1) },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(30.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.White, // White background
                                unfocusedContainerColor = Color.White // White background
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    RecommendedSection()
                }
            }
    }
}


@Composable
fun BrowseByActivitySection() {
    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(
            "Browse by activity",
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = poppinsFontFamily1, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(sampleActivities) { activity ->
                ActivityItem(activity)
            }
        }
    }
}

@Composable
fun ActivityItem(activity: ActivityItemData) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = activity.imageResId),
            contentDescription = activity.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(activity.name, fontFamily = poppinsFontFamily1, fontSize = 13.sp)
    }
}

@Composable
fun RecommendedSection() {
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Recommended",
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = poppinsFontFamily1)
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { /*TODO*/ }) {
                Text("See all", fontFamily = poppinsFontFamily1, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                Icon(Icons.Filled.Refresh, contentDescription = "See all", tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleRecommendedPlaces) { place ->
                RecommendedPlaceCard(place = place, onFavoriteClick = {
                    // In a real app, you'd update the data source
                    place.isFavorite = !place.isFavorite
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedPlaceCard(place: RecommendedPlaceData, onFavoriteClick: () -> Unit) {
    var currentIsFavorite by remember { mutableStateOf(place.isFavorite) }

    Card(
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = place.imageResId),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = {
                        currentIsFavorite = !currentIsFavorite
                        onFavoriteClick()
                    },
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Icon(
                        if (currentIsFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (currentIsFavorite) Color.Red else Color.White
                    )
                }
            }
            Column(modifier = Modifier
                .padding(12.dp)) {
                Text(
                    place.name,
                    fontFamily = poppinsFontFamily1,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Location", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(place.location, fontFamily = poppinsFontFamily1, fontSize = 13.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
