package com.jhainusa.netourism

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jhainusa.netourism.Map.NavigationScreen
import com.jhainusa.netourism.MeshNetworking.ChatViewModel

// Define Poppins font family (assuming poppinsmedium.ttf in res/font)
val poppinsFontFamilyAppNav = FontFamily(
    Font(R.font.manrope_medium)
)

// Screen sealed class now uses @StringRes Int for titles
sealed class Screen(val route: String, @StringRes val title: Int, @DrawableRes val iconResId: Int) {
    object News : Screen("News", R.string.title_news, R.drawable.news)
    object Home : Screen("home", R.string.title_home, R.drawable.home)
    object Map : Screen("map", R.string.title_map, R.drawable.maps)
    object Panic : Screen("panic", R.string.title_panic, R.drawable.panic)
    object Profile : Screen("profile", R.string.title_profile, R.drawable.profile)
}

val bottomNavItemsList = listOf(
    Screen.Home,
    Screen.Map,
    Screen.News,
    Screen.Panic,
    Screen.Profile
)

@Composable
fun AppBottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItemsList.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF55c1f6),
                    selectedTextColor = Color(0xFF55c1f6),
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { 
                    Icon(
                        painter = painterResource(id = screen.iconResId),
                        contentDescription = stringResource(screen.title)
                    )
                },
                label = { Text(stringResource(screen.title), fontFamily = poppinsFontFamilyAppNav) }
            )
        }
    }
}

@Composable
fun AppNavigationHost(navController: NavHostController, innerPadding: PaddingValues,mainNav: NavController,viewModel: ChatViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.News.route) { NewsScreen(navController)} // Assuming OnboardingScreen is the new Login/News screen
        composable(Screen.Home.route) { FirstPageScreen(mainNav) } // Assuming FirstPageScreen is your home screen content
        composable(Screen.Map.route) { NavigationScreen()} //JourneyTimelineScreen(navController) }
        composable(Screen.Panic.route) { SOSScreen(navController,viewModel) }
        composable(Screen.Profile.route) {ProfileScreen(navController)}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(mainNav : NavController,viewModel: ChatViewModel) {
    val navController = rememberNavController()
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp,0.dp,0.dp,0.dp),
        bottomBar = { AppBottomNavigationBar(navController = navController)}
    ) { innerPadding ->
        AppNavigationHost(navController = navController, innerPadding = innerPadding,mainNav,viewModel)
    }
}
