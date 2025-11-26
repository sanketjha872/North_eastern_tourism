package com.jhainusa.netourism

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import kotlinx.coroutines.launch

// List of supported language codes
val supportedLanguages = listOf("en", "hi", "bn", "mr", "te", "ta", "gu", "ur", "kn", "or", "ml")

// Map of language codes to their display names in their own script
val languageMap = mapOf(
    "en" to "English",
    "hi" to "हिन्दी",
    "bn" to "বাংলা",
    "mr" to "मराठी",
    "te" to "తెలుగు",
    "ta" to "தமிழ்",
    "gu" to "ગુજરાતી",
    "ur" to "اردو",
    "kn" to "ಕನ್ನಡ",
    "or" to "ଓଡ଼ିଆ",
    "ml" to "മലയാളം"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    val prefsManager = UserPreferencesManager(context)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.select_language),
                    fontFamily = poppinsFontFamily1,
                ) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 5.dp)
        ) {
            items(supportedLanguages) { langCode ->
                val displayName = languageMap[langCode] ?: langCode
                Text(
                    text = displayName,
                    fontFamily = poppinsFontFamily1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                prefsManager.saveLanguage(langCode)
                                val appLocale = LocaleListCompat.forLanguageTags(langCode)
                                // This will trigger an activity recreation to apply the new language
                                AppCompatDelegate.setApplicationLocales(appLocale)
                            }
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}
