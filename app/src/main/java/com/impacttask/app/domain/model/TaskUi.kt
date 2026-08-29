package com.impacttask.app.domain.model

import com.impacttask.Gain
import com.impacttask.MonsterState
import com.impacttask.TaskCalculator
import com.impacttask.ThreatTier
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.data.model.TaskTimeType
import java.time.LocalDateTime
import kotlin.math.roundToInt

data class SubtaskUi(
    val id: String,
    val title: String,
    val done: Boolean,
    val position: Int,
)

/**
 * The screen-ready shape of a task: tier and monster state are derived here
 * (pure functions of difficulty/impact/dueAt+now, see lib's ImpactTaskDomain),
 * never persisted, so they're always correct without a background job to
 * keep them in sync.
 */
data class TaskUi(
    val id: String,
    val title: String,
    val notes: String,
    val difficulty: Int,
    val impact: Int,
    val timeType: TaskTimeType,
    val dueAt: LocalDateTime?,
    val estimasiMenit: Int?,
    val status: TaskStatus,
    val completedAt: LocalDateTime?,
    val cancelledAt: LocalDateTime?,
    val expAwarded: Int?,
    val subtasks: List<SubtaskUi>,
    val allocations: Map<Gain, Int>,
) {
    val score: Int get() = (difficulty + impact).coerceIn(0, 200)
    val tier: ThreatTier get() = ThreatTier.fromScore(score)
    val state: MonsterState get() = MonsterState.fromDeadline(dueAt, LocalDateTime.now())
    val priorityScore: Int get() = TaskCalculator.priorityScore(tier, state)

    val hpPercent: Int
        get() {
            if (status == TaskStatus.DONE) return 0
            if (subtasks.isEmpty()) return 100
            val doneCount = subtasks.count { it.done }
            return ((1 - doneCount.toDouble() / subtasks.size) * 100).roundToInt()
        }

    val allSubtasksDone: Boolean
        get() = subtasks.isNotEmpty() && subtasks.all { it.done }

    val primaryGain: Gain?
        get() = allocations.maxByOrNull { it.value }?.key
}
