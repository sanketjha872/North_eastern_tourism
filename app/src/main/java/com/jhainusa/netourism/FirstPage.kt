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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

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

val sampleRecommendedPlaces = listOf(
    RecommendedPlaceData("Nongriat Village", R.drawable.bridge, "Shillong", 7, false),
    RecommendedPlaceData("Mountain Trek", R.drawable.boats, "Cherapunji", 12, true),
    RecommendedPlaceData("Elephant Falls", R.drawable.nohakili, "Shillong", 5, false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun FirstPageScreen(mainNav: NavController = rememberNavController()) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(
                            painter = painterResource(R.drawable.location_pin_alt_1_svgrepo_com),
                            contentDescription = stringResource(R.string.location_content_description),
                            tint = Color.DarkGray,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                stringResource(R.string.location),
                                fontFamily = poppinsFontFamily1,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF262626)
                            )
                            Text(
                                stringResource(R.string.safety_level),
                                fontFamily = poppinsFontFamily1,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2BA02D)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { mainNav.navigate("DrawerContent") }) {
                        Icon(
                            painterResource(R.drawable.threeline),
                            contentDescription = stringResource(R.string.notifications),
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(8.dp)
                                .size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.padding(end = 5.dp)

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFF262626),
                shape = CircleShape,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.sos_svgrepo_com),
                    contentDescription = stringResource(R.string.add),
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = Color(0xFFE0F7FA), // Make scaffold background transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Light blue gradient-like background for the top portion
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFa8d7f4),
                                Color.White,
                                Color.White
                            )
                        )
                    )
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
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(R.string.search_your_place), fontFamily = poppinsFontFamily1) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_icon)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
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
                }
                Spacer(modifier = Modifier.height(24.dp))
                QuickActionsSection()
                Spacer(modifier = Modifier.height(24.dp))
                RecommendedSection()
            }
        }
    }
}

@Composable
fun QuickActionsSection() {
    Column(modifier = Modifier.padding(horizontal = 5.dp)) {
        Text(
            stringResource(R.string.quick_actions),
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = poppinsFontFamily1,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickAction(painter = painterResource(R.drawable.police_car_light_svgrepo_com), text = stringResource(R.string.emergency))
            QuickAction(painter = painterResource(R.drawable.police_car_light_svgrepo_com), text = stringResource(R.string.safety_tips))
            QuickAction(painter = painterResource(R.drawable.img_4), text = stringResource(R.string.safe_zones))
            QuickAction(painter = painterResource(R.drawable.police_car_light_svgrepo_com), text = stringResource(R.string.report))
        }
    }
}

@Composable
fun QuickAction(painter: Painter, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* TODO */ }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = text,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontFamily = poppinsFontFamily1,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun RecommendedSection() {
    Column(modifier = Modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.recommended),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = poppinsFontFamily1,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF262626)
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { /*TODO*/ }) {
                Text(stringResource(R.string.see_all), fontFamily = poppinsFontFamily1, fontSize = 16.sp)
                Icon(painterResource(R.drawable.right), contentDescription = stringResource(R.string.see_all), tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleRecommendedPlaces) { place ->
                RecommendedPlaceCard(place = place)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedPlaceCard(place: RecommendedPlaceData) {

    Card(
        modifier = Modifier.width(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xE5F7F8F9)
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = place.imageResId),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(17.dp))
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    place.name,
                    fontFamily = poppinsFontFamily1,
                    fontSize = 16.sp,
                    maxLines = 1,
                    color = Color(0xFF262626),
                    fontWeight = FontWeight.W700,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.location_pin_alt_1_svgrepo_com), contentDescription = stringResource(id = R.string.location_content_description), modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(place.location, fontFamily = poppinsFontFamily1, fontSize = 13.sp, color = Color.DarkGray)
                }
            }
        }
    }
}
