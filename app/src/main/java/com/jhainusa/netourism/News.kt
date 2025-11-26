package com.jhainusa.netourism

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
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
import com.jhainusa.netourism.ui.theme.blue

// Assuming poppinsmedium.ttf is in res/font
val poppinsNews = FontFamily(
    Font(R.font.manrope_medium)
)

data class NewsArticle(
    val id: String,
    @DrawableRes val imageResId: Int,
    val isSafetyAlert: Boolean,
    val location: String,
    val title: String,
    val description: String,
    val time: String,
    val category: String? = null
)

val sampleNews = listOf(
    NewsArticle(
        id = "1",
        imageResId = R.drawable.screenshot_2025_09_11_222500, // Replace
        isSafetyAlert = true,
        location = "SIKKIM",
        title = "Major Landslide on NH10",
        description = "A major landslide near Sevoke has completely blocked Nation...",
        time = "30m ago"
    ),
    NewsArticle(
        id = "2",
        imageResId = R.drawable.screenshot_2025_09_11_222514, // Replace
        isSafetyAlert = true,
        location = "MANIPUR",
        title = "Curfew Imposed in Imphal",
        description = "Authorities have imposed a curfew in parts of Imphal...",
        time = "1h ago"
    ),
    NewsArticle(
        id = "3",
        imageResId = R.drawable.screenshot_2025_09_11_222534, // Replace
        isSafetyAlert = false,
        location = "Nagaland",
        title = "Hornbill Festival Begins",
        description = "The annual Hornbill Festival, a celebration of Nagaland's rich...",
        time = "2h ago",
        category = "Nagaland"
    ),
    NewsArticle(
        id = "4",
        imageResId = R.drawable.screenshot_2025_09_11_222500, // Replace
        isSafetyAlert = false,
        location = "Sikkim",
        title = "Sikkim Tourism Sees Growth",
        description = "Sikkim is witnessing a surge in tourist arrivals this season...",
        time = "4h ago",
        category = "Sikkim"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("Safety Alerts") }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp,0.dp),
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = {
                        Text(
                            "Local News & Alerts",
                            fontFamily = poppinsNews,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )


                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Settings */ }) {
                            Icon(painter = painterResource(R.drawable.news_feed_2_svgrepo_com), contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        },
        containerColor = Color(0xFFFBFBF9)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf("All", "Safety Alerts","Weather", "Travel", "Culture")
                items(chips,){ chipText ->
                    val isSelected = selectedChip == chipText
                    Chip(
                        label = chipText,
                        isSelected = isSelected,
                        onClick = { selectedChip = chipText },
                        isSafetyAlert = chipText == "Safety Alerts"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            // News List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleNews) { article ->
                    NewsCard(article = article)
                }
            }
        }
    }
}

@Composable
fun Chip(label: String, isSelected: Boolean, onClick: () -> Unit, isSafetyAlert: Boolean) {
    FilterChip(
        selected = isSelected,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick,
        label = { Text(label, fontFamily = poppinsNews) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = blue,
            selectedLabelColor = Color.White,
            containerColor = Color.White,
        )
    )
}


@Composable
fun NewsCard(article: NewsArticle) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (article.isSafetyAlert) {
                    Modifier.border(1.dp, Color.Red.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = article.imageResId),
                contentDescription = article.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (article.isSafetyAlert) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Safety Alert",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SAFETY ALERT: ${article.location}",
                            color = Color.Red.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = poppinsNews
                        )
                    }
                } else {
                    Text(
                        text = article.category ?: article.location,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppinsNews
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = poppinsNews
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = article.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = poppinsNews
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = article.time,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = poppinsNews
                    )
                    Icon(
                        painter = painterResource(R.drawable.bookmark_svgrepo_com__2_),
                        contentDescription = "Bookmark",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NETourismTheme {
        NewsScreen(rememberNavController())
    }
}
