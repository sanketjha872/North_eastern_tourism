package com.jhainusa.netourism

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jhainusa.netourism.SupaBase.ReportViewModel

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FirstPageScreen(
    mainNav: NavController,
    navController: NavController,
    viewModel: ReportViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),   // 🟢 IMPORTANT
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.padding(end = 5.dp)
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF262626),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.sos_svgrepo_com),
                    contentDescription = stringResource(R.string.add),
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->

        // 🟢 FIXED: Background should wrap everything
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFa8d7f4),
                            Color.White,
                            Color.White
                        )
                    )
                )
                .padding(paddingValues)    // 🟢 Scaffold padding applied correctly
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            stringResource(R.string.search_your_place),
                            fontFamily = poppinsFontFamily1
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = stringResource(R.string.search_icon)
                        )
                    },
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
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
                QuickActionsSection()
                Spacer(modifier = Modifier.height(24.dp))

                RecommendedSection(
                    navController = navController,
                    animatedVisibilityScope = animatedVisibilityScope
                )
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
            QuickAction(painter = painterResource(R.drawable.home_svgrepo_com__6_), text = stringResource(R.string.emergency))
            QuickAction(painter = painterResource(R.drawable.info_svgrepo_com), text = stringResource(R.string.safety_tips))
            QuickAction(painter = painterResource(R.drawable.img_4), text = stringResource(R.string.safe_zones))
            QuickAction(painter = painterResource(R.drawable.map_tag_svgrepo_com), text = stringResource(R.string.report))
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
            Icon(
                painter = painter,
                contentDescription = text,
                modifier = Modifier.size(36.dp),
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontFamily = poppinsFontFamily1,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RecommendedSection(navController: NavController, animatedVisibilityScope: AnimatedVisibilityScope) {
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
                RecommendedPlaceCard(place = place, navController = navController, animatedVisibilityScope = animatedVisibilityScope)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RecommendedPlaceCard(place: RecommendedPlaceData, navController: NavController, animatedVisibilityScope: AnimatedVisibilityScope) {

    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { navController.navigate("place_details/${place.name}/${place.imageResId}/${place.location}") },
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
                        .sharedElement(
                            rememberSharedContentState(key = "image/${place.imageResId}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
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