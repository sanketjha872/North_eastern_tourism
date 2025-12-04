package com.jhainusa.netourism

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jhainusa.netourism.R
import com.jhainusa.netourism.ui.theme.NETourismTheme
import com.jhainusa.netourism.ui.theme.back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeStayScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Homestay", fontFamily = manropeMedium, fontWeight = FontWeight.Bold) },
                actions = {
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = back
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            FindHomestaySection()
            Spacer(modifier = Modifier.height(24.dp))
            ExploreByCategorySection()
            Spacer(modifier = Modifier.height(24.dp))
            TrendingNearYouSection(navController)
        }
    }
}

@Composable
fun FindHomestaySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(250.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hs), // Placeholder
                contentDescription = "Homestay",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "Find Your Homestay in the Northeast",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = manropeMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun ExploreByCategorySection() {
    Column {
        Text("Explore by Category", fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = manropeMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item { CategoryChip("Mountain Stays") }
            item { CategoryChip("Bamboo Cottages") }
            item { CategoryChip("Tribal Villages") }
        }
    }
}

@Composable
fun CategoryChip(text: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E0))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontFamily = manropeMedium
        )
    }
}

@Composable
fun TrendingNearYouSection(navController: NavController) {
    Column {
        Text("Trending Near You", fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = manropeMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item { TrendingCard(R.drawable.hs1, "Riverside Serenity", "5 km away", "₹750 / night", 4.8f){
                navController.navigate("HomeStayDetailsScreen")
            } }
            item { TrendingCard(R.drawable.hs2, "The Tea Leaf", "12 km away", "₹580 / night", 4.9f){
                navController.navigate("HomeStayDetailsScreen")
            } }
            item { TrendingCard(R.drawable.hs3, "The Tea Leaf", "12 km away", "₹580 / night", 4.9f){
                navController.navigate("HomeStayDetailsScreen")
            } }

        }
    }
}

@Composable
fun TrendingCard(imageRes: Int, title: String, distance: String, price: String, rating: Float,
                 onClick : () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.background(Color.White)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = manropeMedium)
                Text(distance, color = Color.Gray, fontSize = 12.sp, fontFamily = manropeMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(price, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = manropeMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(rating.toString(), fontWeight = FontWeight.Bold, fontFamily = manropeMedium)
                    }
                }
            }
        }
    }
}

