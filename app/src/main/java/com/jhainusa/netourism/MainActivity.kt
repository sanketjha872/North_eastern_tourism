package com.jhainusa.netourism

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.jhainusa.netourism.MeshNetworking.MeshCore
import com.jhainusa.netourism.SupaBase.ReportViewModel
import com.jhainusa.netourism.SupaBase.ReportViewModelFactory
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import com.jhainusa.netourism.ui.theme.NETourismTheme

class MainActivity : AppCompatActivity() {
    private val prefsManager: UserPreferencesManager by lazy {
        UserPreferencesManager(this)
    }

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
        // Set language before UI is created
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()

        val reportViewModel = ViewModelProvider(
            this,
            ReportViewModelFactory(prefsManager)
        ).get(ReportViewModel::class.java)

        prefsManager.getLanguage()?.let {
            val appLocale = LocaleListCompat.forLanguageTags(it)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
        super.onCreate(savedInstanceState)

        val launcher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }


        launcher.launch(permissions.toTypedArray())

        // Initialize mesh networking (only once)
        MeshCore.init(applicationContext, prefsManager)

        setContent {
            val navController = rememberNavController()

            NETourismTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = "SplashScreen",
                        enterTransition = {
                            slideInHorizontally { it } + fadeIn(tween(200))
                        },
                        exitTransition = {
                            slideOutHorizontally { -it } + fadeOut(tween(200))
                        }
                    ) {
                        composable("SplashScreen") {
                            SplashScreen(navController, prefsManager)
                        }
                        composable("login") {
                            SecureLoginScreen(navController, viewModel = reportViewModel)
                        }
                        composable("news") {
                            NewsScreen(navController)
                        }
                        composable("DrawerContent") {
                            DrawerContent(navController = navController)
                        }
                        composable("AllScreenNav") {
                            MainApp(navController, MeshCore.chatViewModel, reportViewModel)
                        }
                        composable("OnboardingScreen") {
                            OnboardingScreen(navController)
                        }
                        composable("LanguageSelectionScreen") {
                            LanguageSelectionScreen(navController)
                        }
                    }
                }
            }
        }
    }
}