package com.impacttask.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.impacttask.Gain

/** A task's EXP allocation across up to 3 Gains (prd.md section 03); percentages sum to 100. */
@Entity(
    tableName = "task_gains",
    primaryKeys = ["taskId", "gainId"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("taskId")],
)
data class TaskGainEntity(
    val taskId: String,
    val gainId: Gain,
    val percent: Int,
)
