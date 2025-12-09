package com.jhainusa.netourism

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jhainusa.netourism.LottieAnimation.LottieLoadingScreen
import com.jhainusa.netourism.News.GNewsArticle
import com.jhainusa.netourism.News.NewsViewModel
import com.jhainusa.netourism.ui.theme.NETourismTheme
import com.jhainusa.netourism.ui.theme.blue
import kotlinx.coroutines.delay

// Assuming poppinsmedium.ttf is in res/font
val poppinsNews = FontFamily(
    Font(R.font.manrope_medium)
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController,
               viewModel: NewsViewModel = viewModel()) {
    var selectedChip by remember { mutableStateOf("Safety Alerts") }
    val isLoading by viewModel.isLoading.collectAsState()


    val newsList by viewModel.news.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNews()
    }
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
        containerColor = (Color(0xFFF4F8FA))

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf("All", "Safety Alerts", "Weather", "Travel", "Culture")
                items(chips,) { chipText ->
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
            if (isLoading) {
                LottieLoadingScreen("Searching.json")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(newsList) { article ->
                        NewsCard(article = article)
                    }
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
fun NewsCard(article: GNewsArticle) {
    val uriHandler = LocalUriHandler.current

    Card(
        onClick = {
            uriHandler.openUri(article.url)
        },
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserImage(
                article.image,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
//                if (article.isSafetyAlert) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(
//                            Icons.Default.Warning,
//                            contentDescription = "Safety Alert",
//                            tint = Color.Red.copy(alpha = 0.8f),
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text(
//                            text = "SAFETY ALERT: ${article.location}",
//                            color = Color.Red.copy(alpha = 0.8f),
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = poppinsNews
//                        )
//                    }
//                } else {
                    Text(
                        text = article.source.name,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = poppinsNews
                    )

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

                article.description?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = poppinsNews
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = article.publishedAt.take(10)+ "\t\t" +article.publishedAt.takeLast(9),
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
