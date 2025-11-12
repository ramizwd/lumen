package com.example.lumen.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInQuint
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lumen.domain.ble.model.ConnectionState
import com.example.lumen.presentation.MainViewModel
import com.example.lumen.presentation.ble.discovery.DiscoverDevicesScreen
import com.example.lumen.presentation.ble.led_control.LedControlScreen
import com.example.lumen.presentation.common.components.LoadingOverlay

/**
 * Top-level navigation graph
 */
@Composable
fun LumenNavHost() {
    val rootNavController = rememberNavController()

    val mainViewModel = hiltViewModel<MainViewModel>()
    val connectionState by mainViewModel.connectionState.collectAsStateWithLifecycle()
    val loadingText by mainViewModel.loadingText.collectAsStateWithLifecycle()
    val showLoading by mainViewModel.showLoading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = connectionState) {
        val currentRoute = rootNavController.currentBackStackEntry?.destination?.route

        if (connectionState == ConnectionState.STATE_LOADED_AND_CONNECTED &&
            currentRoute != LedControlScreen::class.qualifiedName) {
            rootNavController.navigate(LedControlScreen) {
                popUpTo(DiscoverDevicesScreen)
                launchSingleTop = true
            }
        } else if (connectionState == ConnectionState.DISCONNECTED &&
            currentRoute != DiscoverDevicesScreen::class.qualifiedName) {
            rootNavController.navigate(DiscoverDevicesScreen) {
                popUpTo(LedControlScreen)
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = rootNavController, startDestination = DiscoverDevicesScreen,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {

        composable<DiscoverDevicesScreen> {
            DiscoverDevicesScreen()

            LoadingOverlay(
                text = loadingText,
                isVisible = showLoading,
                onDismiss = mainViewModel::disconnect,
            )
        }

        composable<LedControlScreen>(
            enterTransition = {
                fadeIn(
                    animationSpec = tween(500, easing = EaseInQuint)
                ) +
                        slideIntoContainer(
                    animationSpec = tween(400, easing = EaseInQuint),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = LinearEasing)
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            LedControlScreen(rootNavController = rootNavController)
        }
    }
}