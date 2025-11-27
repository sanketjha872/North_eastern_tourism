package com.jhainusa.netourism

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Set language before UI is created
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
                    NavHost(
                        navController = navController,
                        startDestination =
                        if (prefsManager.hasUser()) "AllScreenNav"
                        else "OnboardingScreen",
                    ) {
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
