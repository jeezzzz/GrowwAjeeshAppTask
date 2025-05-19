package `in`.groww.ajeeshapptask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import `in`.groww.ajeeshapptask.ui.presentation.detail.screen.DetailScreen
import `in`.groww.ajeeshapptask.ui.presentation.explore.screen.ExploreScreen
import `in`.groww.ajeeshapptask.ui.presentation.listing.screen.ListingScreen
import `in`.groww.ajeeshapptask.ui.presentation.search.screen.SearchScreen
import `in`.groww.ajeeshapptask.ui.theme.GrowwAjeeshAppTaskTheme
import `in`.groww.ajeeshapptask.ui.utils.ThemeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp(themeViewModel: ThemeViewModel = hiltViewModel()) {
    val systemDark = isSystemInDarkTheme()
    val userDarkMode by themeViewModel.isDarkMode.collectAsState()
    val isDarkMode = userDarkMode ?: systemDark

    val navController = rememberNavController()

    GrowwAjeeshAppTaskTheme(darkTheme = isDarkMode) {
        NavHost(
            navController = navController,
            startDestination = "explore"
        ) {
            composable("explore") {
                ExploreScreen(
                    isDarkMode = isDarkMode,
                    navController = navController,
                    onThemeToggle = {
                        // Toggle between dark/light, or reset to system
                        if (userDarkMode == null || userDarkMode == false) {
                            themeViewModel.setDarkMode(true)
                        } else {
                            themeViewModel.setDarkMode(false)
                        }
                    },
                    onViewAllClick = { category ->
                        navController.navigate("listing/$category")
                    },
                    onStockClick = { symbol ->
                        navController.navigate("detail/$symbol")
                    }
                )
            }
            composable("listing/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category").orEmpty()
                ListingScreen(
                    category = category,
                    onStockClick = { symbol ->
                        navController.navigate("detail/$symbol")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("detail/{symbol}") { backStackEntry ->
                val symbol = backStackEntry.arguments?.getString("symbol").orEmpty()
                DetailScreen(
                    symbol = symbol,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("search") {
                SearchScreen(
                    navController = navController,
                    onSymbolSelected = { symbol ->
                        navController.navigate("detail/$symbol") {
                            popUpTo("search") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
