package com.khodier.hotelexplorer.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.khodier.hotelexplorer.core.designsystem.R
import com.khodier.hotelexplorer.features.booking.BookingScreen
import com.khodier.hotelexplorer.features.booking.BookingSuccessScreen
import com.khodier.hotelexplorer.features.favorites.FavoritesScreen
import com.khodier.hotelexplorer.features.hoteldetails.HotelDetailsScreen
import com.khodier.hotelexplorer.features.hotels.HotelsScreen
import com.khodier.hotelexplorer.features.booking.BookingViewModel


sealed class Screen(val route: String) {
    object Hotels : Screen("hotels")
    object Favorites : Screen("favorites")
    object BookingSuccess : Screen("booking_success")
    object HotelDetails : Screen("hotel/{hotel_id}") {
        fun go(id: Long) = "hotel/$id"
    }


    object Booking : Screen("booking/{hotel_id}") {
        fun go(id: Long) = "booking/$id"
    }

    object BookingFlow : Screen("booking_flow/{hotel_id}") {
        fun go(id: Long) = "booking_flow/$id"
    }
}


data class BottomNavItem(val screen: Screen, val label: Int, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Hotels, R.string.search_hotels, Icons.Outlined.Home),
    BottomNavItem(Screen.Favorites, R.string.favorites, Icons.Default.FavoriteBorder),
)

private val mainRoutes = bottomNavItems.map { it.screen.route }.toSet()

private val EnterTransition = fadeIn(tween(180))
private val ExitTransition = fadeOut(tween(100))
private val PopEnterTransition = fadeIn(tween(180))
private val PopExitTransition = fadeOut(tween(100))

@Composable
fun HotelExplorerNavHost(
    startDestination: String = "main"
) {
    val navController = rememberNavController()
    val backstackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backstackEntry?.destination?.route

    val showNavigation = currentRoute != null && currentRoute in mainRoutes

    val initialRoute = remember(startDestination) {
        if (startDestination == "main") Screen.Hotels.route else startDestination
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (showNavigation) {
                    BottomNav(currentRoute = currentRoute) { screen ->
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    this.saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = initialRoute,
                modifier = Modifier.padding(padding),
                enterTransition = { EnterTransition },
                exitTransition = { ExitTransition },
                popEnterTransition = { PopEnterTransition },
                popExitTransition = { PopExitTransition }
            ) {

                composable(Screen.Hotels.route) {
                    HotelsScreen(
                        onHotelClick = { navController.navigate(Screen.HotelDetails.go(it)) }
                    )
                }

                composable(Screen.Favorites.route) {
                    FavoritesScreen(
                        onHotelClick = { navController.navigate(Screen.HotelDetails.go(it)) }
                    )
                }

                composable(
                    route = Screen.HotelDetails.route,
                    arguments = listOf(navArgument("hotel_id") { type = NavType.LongType })
                ) {
                    HotelDetailsScreen(
                        onReserveClick = { navController.navigate(Screen.BookingFlow.go(it)) },
                        onBackClick = { navController.popBackStack() },
                    )
                }

                navigation(
                    route = "booking_flow/{hotel_id}",
                    startDestination = Screen.Booking.route,
                    arguments = listOf(navArgument("hotel_id") { type = NavType.LongType })
                ) {
                    composable(Screen.Booking.route) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("booking_flow/{hotel_id}")
                        }
                        val viewModel: BookingViewModel = hiltViewModel(parentEntry)

                        BookingScreen(
                            viewModel = viewModel,
                            onBookingSuccess = {
                                navController.navigate(Screen.BookingSuccess.route)
                            },
                            onBackClick = { navController.popBackStack() },
                        )
                    }

                    composable(Screen.BookingSuccess.route) { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            navController.getBackStackEntry("booking_flow/{hotel_id}")
                        }
                        val viewModel: BookingViewModel = hiltViewModel(parentEntry)

                        BookingSuccessScreen(
                            viewModel = viewModel,
                            onBackHotelsClick = {
                                navController.navigate(Screen.Hotels.route) {
                                    popUpTo("booking_flow/{hotel_id}") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
