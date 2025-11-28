package com.example.lumen.presentation.ble.led_control.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun NavRail(
    navController: NavController,
    currentDestination: NavDestination?,
    windowInsets: WindowInsets,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        windowInsets = windowInsets,
        containerColor = containerColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BottomNavItem.entries.forEach { item ->
                val selected = currentDestination?.hierarchy?.any {
                    it.route == item.route::class.qualifiedName
                } == true

                NavigationRailItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = if (selected) painterResource( item.iconSelected)
                            else painterResource( item.icon),
                            contentDescription = item.contentDescription
                        )
                    },
                    label = { Text(text = item.label) }
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
fun NavRailPreview() {
    LumenTheme {
        Surface {
            NavRail(
                navController = rememberNavController(),
                currentDestination = null,
                windowInsets = NavigationBarDefaults.windowInsets,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
    }
}