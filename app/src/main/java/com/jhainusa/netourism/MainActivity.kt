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
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jhainusa.netourism.MeshNetworking.MeshCore
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import com.jhainusa.netourism.ui.theme.NETourismTheme
import kotlinx.coroutines.launch

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
                        composable("FirstPageScreen") {
                            FirstPageScreen(mainNav = navController)
                        }
                    }
                }
            }
        }

    }
}

// A simple button to toggle the language
@Composable
fun LanguageSwitcher() {
    val context = LocalContext.current
    val prefsManager = UserPreferencesManager(context)
    val scope = rememberCoroutineScope()

    Button(onClick = {
        scope.launch {
            val currentLang = prefsManager.getLanguage() ?: "en"
            val newLang = if (currentLang == "en") "hi" else "en"
            prefsManager.saveLanguage(newLang)

            // Set the app locale. The system will handle recreating the activity.
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLang))
        }
    }) {
        Text("Toggle Language")
    }
}
