package com.jhainusa.netourism

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jhainusa.netourism.ui.theme.NETourismTheme
import com.jhainusa.netourism.ui.theme.blue
import kotlinx.coroutines.launch

val poppins = FontFamily(
    Font(R.font.manrope_medium)
)

data class OnboardingItem(
    val imageRes: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val items = listOf(
        OnboardingItem(R.drawable.img, "Stay Safe Everywhere You Go", "Get real-time safety alerts and geo-fence warnings."),
        OnboardingItem(R.drawable.img_1, "Your Digital Tourist ID", "Secure, blockchain-verified identity for safe travel."),
        OnboardingItem(R.drawable.img_2, "One Tap SOS", "Instantly alert authorities and your emergency contacts.")
    )
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Scaffold(bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {


            Button(
                onClick = {
                    if (pagerState.currentPage < items.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        navController.navigate("login")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (pagerState.currentPage < items.size - 1) "Next" else "Get Started",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontFamily = poppins
                )
            }
        }
    }) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally


        ) {

            TextButton(
                onClick = {navController.navigate("login")},
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Text(text = "Skip", color = Color.Gray, fontFamily = poppins)
            }

            HorizontalPager(
                count = items.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(item = items[page])
            }
            PagerIndicator(size = items.size, currentPage = pagerState.currentPage)

        }
    }
}

@Composable
fun OnboardingPage(item: OnboardingItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
    ) {
        if (item.imageRes != 0) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                modifier = Modifier
                    .size(200.dp)
            )
        } else {
            // Placeholder for SOS button
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text("SOS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = poppins)
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            fontFamily = poppins
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontFamily = poppins
        )
    }
}

@Composable
fun PagerIndicator(size: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        repeat(size) { i ->
            val width = animateDpAsState(targetValue = if (i == currentPage) 24.dp else 8.dp, label = "")
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width.value)
                    .clip(CircleShape)
                    .background(if (i == currentPage) blue else Color.Gray)
            )
            if (i < size - 1) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    NETourismTheme {
        OnboardingScreen(rememberNavController())
    }
}
