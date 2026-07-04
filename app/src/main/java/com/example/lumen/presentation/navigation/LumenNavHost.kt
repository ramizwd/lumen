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
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.MainViewModel
import com.example.lumen.presentation.ble.AboutScreen
import com.example.lumen.presentation.ble.discovery.DiscoverDevicesScreen
import com.example.lumen.presentation.ble.ledcontrol.LedControlScreen
import com.example.lumen.presentation.common.components.LoadingOverlay

/**
 * Top-level navigation graph
 */
@Composable
fun LumenNavHost() {
    val rootNavController = rememberNavController()

    val mainViewModel = hiltViewModel<MainViewModel>()
    val connectionState by mainViewModel.connectionState.collectAsStateWithLifecycle()
    val loadingInfo by mainViewModel.loadingInfo.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = connectionState) {
        val currentRoute = rootNavController.currentBackStackEntry?.destination?.route

        if (connectionState == ConnectionState.STATE_LOADED_AND_CONNECTED &&
            currentRoute != LedControlScreen::class.qualifiedName &&
            currentRoute != AboutScreen::class.qualifiedName
        ) {
            rootNavController.navigate(LedControlScreen) {
                launchSingleTop = true
            }
        } else if (connectionState == ConnectionState.DISCONNECTED &&
            currentRoute != DiscoverDevicesScreen::class.qualifiedName &&
            currentRoute != AboutScreen::class.qualifiedName
        ) {
            rootNavController.navigate(DiscoverDevicesScreen) {
                popUpTo(LedControlScreen) { inclusive = true }
                launchSingleTop = true
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
