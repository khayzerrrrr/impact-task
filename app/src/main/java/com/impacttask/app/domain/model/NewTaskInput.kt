package com.impacttask.app.domain.model

import com.impacttask.Gain
import com.impacttask.app.data.model.TaskTimeType
import java.time.LocalDateTime

data class NewTaskInput(
    val title: String,
    val notes: String = "",
    val difficulty: Int,
    val impact: Int,
    val timeType: TaskTimeType,
    val dueAt: LocalDateTime?,
    val estimasiMenit: Int?,
    /** Percentages across up to 3 Gains; must sum to 100 (enforced by the Create screen). */
    val allocations: Map<Gain, Int>,
)
