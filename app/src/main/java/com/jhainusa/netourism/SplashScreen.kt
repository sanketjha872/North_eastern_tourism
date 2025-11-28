package com.jhainusa.netourism

import android.R.attr.font
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
            .background(
                        Color(0xFFB8DEF7)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Safe Journey",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.manrope_bold))
            )
            Text(
                text = "Travel with confidence.",
                fontSize = 16.sp,
                color = Color.White,
                fontFamily = poppinsFontFamily1
            )
        }
    }
}