package com.impacttask.app.ui.nav

sealed class ImpactDestination(val route: String) {
    data object Tasks : ImpactDestination("tasks")
    data object Gains : ImpactDestination("gains")
    data object Create : ImpactDestination("create")
    data object TaskDetail : ImpactDestination("task/{taskId}") {
        const val ARG_TASK_ID = "taskId"
        fun route(taskId: String) = "task/$taskId"
    }
}

/** The two bottom-nav tabs. Create and Detail are pushed on top, not tabs
 * (mirrors the prototype's IA but trimmed to Fase 1's actual scope). */
val bottomNavDestinations = listOf(ImpactDestination.Tasks, ImpactDestination.Gains)
