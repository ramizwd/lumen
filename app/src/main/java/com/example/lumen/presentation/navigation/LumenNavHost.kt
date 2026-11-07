package com.example.lumen.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInQuint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lumen.presentation.MainViewModel
import com.example.lumen.presentation.ble.AboutScreen
import com.example.lumen.presentation.ble.discovery.DiscoverDevicesScreen
import com.example.lumen.presentation.ble.ledcontrol.LedControlScreen
import com.example.lumen.presentation.common.components.LoadingOverlay
import timber.log.Timber

/**
 * Top-level navigation graph
 */
@Composable
fun LumenNavHost() {
    val rootNavController = rememberNavController()

    val mainViewModel = hiltViewModel<MainViewModel>()
    val loadingInfo by mainViewModel.loadingInfo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        mainViewModel.screenToNavigate.collect {
            val currentRoute = rootNavController.currentBackStackEntry?.destination?.route

            when (it) {
                NavigationEvent.ToControl -> {
                    if (currentRoute != LedControlScreen::class.qualifiedName &&
                        currentRoute != AboutScreen::class.qualifiedName
                    ) {
                        rootNavController.navigate(LedControlScreen) {
                            Timber.tag("LumenNavHost").i("navigated ToControl")
                            launchSingleTop = true
                        }
                    }
                }

                NavigationEvent.ToDiscovery -> {
                    if (currentRoute != DiscoverDevicesScreen::class.qualifiedName &&
                        currentRoute != AboutScreen::class.qualifiedName
                    ) {
                        rootNavController.navigate(DiscoverDevicesScreen) {
                            Timber.tag("LumenNavHost").i("navigated ToDiscovery")
                            popUpTo(LedControlScreen) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }

    NavHost(
        navController = rootNavController,
        startDestination = DiscoverDevicesScreen,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        modifier = Modifier.fillMaxSize(),
    ) {
        composable<DiscoverDevicesScreen> {
            DiscoverDevicesScreen(
                onNavigateToAbout = {
                    rootNavController.navigate(AboutScreen)
                },
                onNavigateToControl = {
                    rootNavController.navigate(LedControlScreen)
                },
            )

            LoadingOverlay(
                text = loadingInfo.text?.asString(),
                isVisible = loadingInfo.isVisible,
                onDismiss = mainViewModel::disconnect,
            )
        }

        composable<AboutScreen>(
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(400, easing = EaseInQuint),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                )
            },
            exitTransition = NavTransitions.exit,
        ) {
            AboutScreen(
                onBackClick = { rootNavController.popBackStack() },
            )
        }

        composable<LedControlScreen>(
            enterTransition = NavTransitions.enter,
            exitTransition = NavTransitions.exit,
        ) {
            LedControlScreen(onBackClick = { rootNavController.popBackStack() })
        }
    }
}
