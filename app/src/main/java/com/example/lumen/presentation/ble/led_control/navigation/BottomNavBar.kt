package com.example.lumen.presentation.ble.led_control.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.example.lumen.presentation.theme.LumenTheme

@Composable
fun BottomNavBar(
    navController: NavController,
    currentDestination: NavDestination?,
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        windowInsets = windowInsets,
        modifier = modifier
    ) {
        BottomNavItem.entries.forEach { item ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == item.route::class.qualifiedName
            } == true

            NavigationBarItem(
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

@Composable
@PreviewLightDark
fun BottomNavBarPreview() {
    LumenTheme {
        Surface {
            BottomNavBar(
                navController = rememberNavController(),
                currentDestination = null,
                windowInsets = NavigationBarDefaults.windowInsets,
            )
        }
    }
}