package com.impacttask.app.data.repository

import com.impacttask.app.domain.model.NewTaskInput
import com.impacttask.app.domain.model.TaskUi
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<TaskUi>>
    fun observeTask(taskId: String): Flow<TaskUi?>

    /** Returns the new task's id. */
    suspend fun createTask(input: NewTaskInput): String

    suspend fun addSubtask(taskId: String, title: String)
    suspend fun setSubtaskDone(taskId: String, subtaskId: String, done: Boolean)

    /**
     * Awards EXP (base * timing * balance, distributed across the task's
     * Gain allocations per prd.md section 07) and marks the task DONE.
     * No-ops if the task isn't ACTIVE, so re-opening a finished task from
     * the activity log can never double-award EXP.
     */
    suspend fun completeTask(taskId: String)

    /** No EXP, no penalty -- the monster is "released", not defeated. */
    suspend fun cancelTask(taskId: String)
}
