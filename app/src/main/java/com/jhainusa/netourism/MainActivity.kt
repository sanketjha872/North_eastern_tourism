package com.jhainusa.netourism

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.google.accompanist.navigation.animation.composable
import com.jhainusa.netourism.Map.LocationService
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
            add(Manifest.permission.BLUETOOTH_ADVERTISE)
            add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
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
            ReportViewModelFactory(prefsManager, applicationContext)
        ).get(ReportViewModel::class.java)

        prefsManager.getLanguage()?.let {
            val appLocale = LocaleListCompat.forLanguageTags(it)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
        super.onCreate(savedInstanceState)

        val launcher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsResult ->
            val fineLocationGranted = permissionsResult.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
            val coarseLocationGranted = permissionsResult.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

            if (fineLocationGranted || coarseLocationGranted) {
                Log.d("MainActivity", "Location permission granted, starting service.")
                Intent(applicationContext, LocationService::class.java).also {
                    startService(it)
                }
            } else {
                Log.e("MainActivity", "Location permission not granted, cannot start service.")
            }

            // Start mesh core only after all bluetooth permissions are granted
            val areBluetoothPermissionsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionsResult.getOrDefault(Manifest.permission.BLUETOOTH_CONNECT, false) &&
                        permissionsResult.getOrDefault(Manifest.permission.BLUETOOTH_SCAN, false) &&
                        permissionsResult.getOrDefault(Manifest.permission.BLUETOOTH_ADVERTISE, false)
            } else true

            if (areBluetoothPermissionsGranted) {
                Log.d("MainActivity", "Bluetooth permissions granted, initializing mesh core.")
                MeshCore.init(applicationContext, prefsManager)
            } else {
                Log.e("MainActivity", "Bluetooth permissions not granted, cannot initialize mesh core.")
            }
        }


        Log.d("MainActivity", "Requesting permissions")
        launcher.launch(permissions.toTypedArray())

        setContent {
            val navController = rememberNavController()

            NETourismTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val startSos = intent.getBooleanExtra("start_sos", false)
                    val sosAction = intent.getStringExtra("sos_action")
                    AnimatedNavHost(
                        navController = navController,
                        startDestination = if (startSos) "AllScreenNav" else "SplashScreen",
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
                        composable("homeStay"){
                            HomeStayScreen(navController)
                        }
                        composable("HomeStayDetailsScreen") {
                            HomeStayDetailsScreen()
                        }
                        composable("DrawerContent") {
                            DrawerContent(navController = navController)
                        }
                        composable("AiSafetyScore") {
                            AiSafetyScore()
                        }
                        composable("DosAndDontsScreen"){
                            DosAndDontsScreen()
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