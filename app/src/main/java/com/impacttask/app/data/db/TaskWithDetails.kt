package com.impacttask.app.data.db

import androidx.room.Embedded
import androidx.room.Relation
import com.impacttask.app.data.db.entity.SubtaskEntity
import com.impacttask.app.data.db.entity.TaskEntity
import com.impacttask.app.data.db.entity.TaskGainEntity

data class TaskWithDetails(
    @Embedded val task: TaskEntity,
    @Relation(parentColumn = "id", entityColumn = "taskId")
    val subtasks: List<SubtaskEntity>,
    @Relation(parentColumn = "id", entityColumn = "taskId")
    val allocations: List<TaskGainEntity>,
)
