package com.jhainusa.netourism

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jhainusa.netourism.ui.theme.back
import com.jhainusa.netourism.ui.theme.blue

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaceDetailsScreen(
    navController: NavController,
    placeName: String?,
    placeImageResId: Int?,
    placeLocation : String?,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    if (placeName == null || placeImageResId == null) {
        // Handle error case
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(back)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .sharedElement(
                    rememberSharedContentState(key = "image/$placeImageResId"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xE5F7F8F9)
            )
        ) {
            Image(
                painter = painterResource(id = placeImageResId),
                contentDescription = placeName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Card() {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                back,
                                back
                            )
                        )
                    )
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 100.dp)
                    .sharedElement(
                        rememberSharedContentState(key = "text/$placeName"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    Row(
                        modifier = Modifier.padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = placeName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = poppinsFontFamily1,
                                color = Color(0xFF262626)
                            )
                            Spacer(modifier = Modifier.height(5.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painterResource(R.drawable.location_pin_alt_1_svgrepo_com),
                                    contentDescription = "",
                                    tint = Color(0xFF6F7A8A),
                                    modifier = Modifier.size(18.dp)
                                )
                                if (placeLocation != null) {
                                    Text(
                                        text = placeLocation,
                                        fontSize = 14.sp,
                                        fontFamily = poppinsFontFamily1,
                                        color = Color(0xFF6F7A8A)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {},
                            modifier = Modifier.clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                contentDescription = "",
                                tint = Color.Black,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    StatsRow()

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "$placeName is a serene village in Meghalaya, famous for its living root bridges, including the iconic Double Decker Bridge. It offers breathtaking nature trails, crystal-clear streams.",
                        fontSize = 14.sp,
                        fontFamily = poppinsFontFamily1,
                        color = Color(0xFF5A5A5A),
                        lineHeight = 23.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    StartJourneyButton()
                }
            }
        }
    }

}

@Composable
fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth()
            . clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(title = "Total Distance", value = "26 km")
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .background(Color.LightGray)
        )
        StatItem(title = "Weather", value = "12°c")
        Box(
            modifier = Modifier
                .width(0.5.dp)
                .height(40.dp)
                .background(Color.LightGray)
        )
        StatItem(title = "Close At", value = "7 pm")
    }
}

@Composable
fun StatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontFamily = poppinsFontFamily1,
            color = Color(0xFF7B8A97)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontFamily = poppinsFontFamily1,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
fun StartJourneyButton() {
    Button(
        onClick = {},
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)
            .height(50.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = blue
        ),
    ) {
        Text(
            text = "Start Journey",
            fontSize = 16.sp,
            fontFamily = poppinsFontFamily1,
            color = Color.White
        )
    }
}

