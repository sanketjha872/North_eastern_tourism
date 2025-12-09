package com.jhainusa.netourism

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, prefsManager: UserPreferencesManager) {
    LaunchedEffect(Unit) {
        delay(1500)
        val destination = if (prefsManager.hasUser()) "AllScreenNav" else "OnboardingScreen"
        navController.navigate(destination) {
            popUpTo("SplashScreen") {
                inclusive = true
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(30.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.gemini_generated_image_d1nfc1d1nfc1d1nf__1_),
                contentDescription = null
            )
        }
    }
}