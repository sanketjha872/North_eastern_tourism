package com.jhainusa.netourism

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jhainusa.netourism.MeshNetworking.ChatScreen
import com.jhainusa.netourism.MeshNetworking.ChatViewModel
import com.jhainusa.netourism.MeshNetworking.MeshCore

import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import com.jhainusa.netourism.ui.theme.NETourismTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var prefsManager: UserPreferencesManager

    private val permissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO

    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
    }


    @OptIn(ExperimentalAnimationApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = UserPreferencesManager(this)


        val launcher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }


        launcher.launch(permissions.toTypedArray())

        // Initialize mesh networking (only once)
        MeshCore.init(applicationContext,prefsManager)

        setContent {
            val navController = rememberAnimatedNavController()

            NETourismTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    AnimatedNavHost(

                        navController = navController,
                        startDestination = 
                            if (prefsManager.hasUser()) "AllScreenNav"
                            else "OnboardingScreen",

                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        }
                    ) {
                        composable("login") {
                            SecureLoginScreen(navController)
                        }
                        composable("userinfo") {
                            SecureLoginScreen(navController)
                        }
                        composable("news") {
                            NewsScreen(navController)
                        }
                        composable("DrawerContent") {
                            DrawerContent()
                        }
                        composable("AllScreenNav") {
                            MainApp(navController, MeshCore.chatViewModel)
                        }
                        composable("OnboardingScreen") {
                            OnboardingScreen(navController)
                        }
                    }
                }
            }
        }

    }
}

