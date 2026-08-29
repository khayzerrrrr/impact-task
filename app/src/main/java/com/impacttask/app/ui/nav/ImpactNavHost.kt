package com.impacttask.app.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.impacttask.app.ui.create.CreateTaskScreen
import com.impacttask.app.ui.detail.TaskDetailScreen
import com.impacttask.app.ui.gains.GainsScreen
import com.impacttask.app.ui.tasks.TaskListScreen

@Composable
fun ImpactNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination

            // Only show the tab bar on top-level destinations, not on Create/Detail.
            val isTopLevel = bottomNavDestinations.any { dest ->
                currentRoute?.hierarchy?.any { it.route == dest.route } == true
            }
            if (isTopLevel) {
                NavigationBar {
                    bottomNavDestinations.forEach { destination ->
                        val selected = currentRoute?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = when (destination) {
                                        ImpactDestination.Tasks -> Icons.Filled.List
                                        ImpactDestination.Gains -> Icons.Filled.Star
                                        else -> Icons.Filled.List
                                    },
                                    contentDescription = null,
                                )
                            },
                            label = {
                                Text(
                                    when (destination) {
                                        ImpactDestination.Tasks -> "Tasks"
                                        ImpactDestination.Gains -> "Gains"
                                        else -> ""
                                    },
                                )
                            },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ImpactDestination.Tasks.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(ImpactDestination.Tasks.route) {
                TaskListScreen(
                    onOpenTask = { id -> navController.navigate(ImpactDestination.TaskDetail.route(id)) },
                    onCreateTask = { navController.navigate(ImpactDestination.Create.route) },
                )
            }
            composable(ImpactDestination.Gains.route) {
                GainsScreen()
            }
            composable(ImpactDestination.Create.route) {
                CreateTaskScreen(
                    onBack = { navController.popBackStack() },
                    onCreated = { id ->
                        navController.navigate(ImpactDestination.TaskDetail.route(id)) {
                            popUpTo(ImpactDestination.Tasks.route)
                        }
                    },
                )
            }
            composable(
                route = ImpactDestination.TaskDetail.route,
                arguments = listOf(
                    navArgument(ImpactDestination.TaskDetail.ARG_TASK_ID) { type = NavType.StringType },
                ),
            ) {
                TaskDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
