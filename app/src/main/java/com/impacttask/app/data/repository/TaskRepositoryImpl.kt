package com.impacttask.app.data.repository

import androidx.room.withTransaction
import com.impacttask.Gain
import com.impacttask.TaskCalculator
import com.impacttask.app.data.db.ExpLedgerDao
import com.impacttask.app.data.db.GainDao
import com.impacttask.app.data.db.ImpactDatabase
import com.impacttask.app.data.db.TaskDao
import com.impacttask.app.data.db.TaskWithDetails
import com.impacttask.app.data.db.entity.ExpLedgerEntity
import com.impacttask.app.data.db.entity.ExpReason
import com.impacttask.app.data.db.entity.GainEntity
import com.impacttask.app.data.db.entity.SubtaskEntity
import com.impacttask.app.data.db.entity.TaskEntity
import com.impacttask.app.data.db.entity.TaskGainEntity
import com.impacttask.app.data.identity.OwnerIdProvider
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.domain.model.NewTaskInput
import com.impacttask.app.domain.model.SubtaskUi
import com.impacttask.app.domain.model.TaskUi
import com.impacttask.app.util.toEpochMillis
import com.impacttask.app.util.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val db: ImpactDatabase,
    private val taskDao: TaskDao,
    private val gainDao: GainDao,
    private val ledgerDao: ExpLedgerDao,
    private val ownerIdProvider: OwnerIdProvider,
) : TaskRepository {

    override fun observeTasks(): Flow<List<TaskUi>> = flow {
        val ownerId = ownerIdProvider.getOwnerId()
        emitAll(taskDao.observeTasks(ownerId))
    }.map { rows -> rows.map { it.toUi() } }

    override fun observeTask(taskId: String): Flow<TaskUi?> =
        taskDao.observeTask(taskId).map { it?.toUi() }

    override suspend fun createTask(input: NewTaskInput): String {
        val ownerId = ownerIdProvider.getOwnerId()
        val task = TaskEntity(
            ownerId = ownerId,
            title = input.title,
            notes = input.notes,
            difficulty = input.difficulty,
            impact = input.impact,
            timeType = input.timeType,
            dueAt = input.dueAt?.toEpochMillis(),
            estimasiMenit = input.estimasiMenit,
        )
        val allocations = input.allocations.map { (gain, percent) ->
            TaskGainEntity(taskId = task.id, gainId = gain, percent = percent)
        }
        db.withTransaction {
            taskDao.upsertTask(task)
            taskDao.upsertAllocations(allocations)
        }
        return task.id
    }

    override suspend fun addSubtask(taskId: String, title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        db.withTransaction {
            val position = taskDao.countSubtasks(taskId)
            taskDao.upsertSubtask(SubtaskEntity(taskId = taskId, title = trimmed, position = position))
        }
    }

    override suspend fun setSubtaskDone(taskId: String, subtaskId: String, done: Boolean) {
        val current = taskDao.observeTask(taskId).first() ?: return
        val subtask = current.subtasks.find { it.id == subtaskId } ?: return
        taskDao.upsertSubtask(subtask.copy(done = done))
    }

    override suspend fun completeTask(taskId: String) {
        val ownerId = ownerIdProvider.getOwnerId()
        val current = taskDao.observeTask(taskId).first() ?: return
        if (current.task.status != TaskStatus.ACTIVE) return

        val now = LocalDateTime.now()
        val base = TaskCalculator.baseExperience(current.task.difficulty, current.task.impact)
        val timeMultiplier = TaskCalculator.timeMultiplier(current.task.dueAt?.toLocalDateTime(), now)
        // Streak tracking is a Fase 4 feature (prd.md roadmap); no bonus applied yet.
        val streakMultiplier = TaskCalculator.streakMultiplier(0)
        val total = (base * timeMultiplier * streakMultiplier).roundToInt()

        val allocations = current.allocations.associate { it.gainId to it.percent }
        val perGainBase = TaskCalculator.distributeExperience(total, allocations)

        // Gains are normally seeded when the Gains screen first loads (see
        // GainRepositoryImpl); guard here too so completing a task before ever
        // opening that screen can't silently no-op the EXP UPDATE below.
        gainDao.insertIfAbsent(Gain.entries.map { GainEntity(id = it, ownerId = ownerId, totalExp = 0) })
        val gainLevels = gainDao.observeGains(ownerId).first()
            .associate { it.id to TaskCalculator.levelProgress(it.totalExp).level }

        db.withTransaction {
            var awarded = 0
            perGainBase.forEach { (gain, amount) ->
                val multiplier = TaskCalculator.balanceMultiplier(gain, gainLevels)
                val finalAmount = (amount * multiplier).roundToInt()
                awarded += finalAmount
                gainDao.addExp(ownerId, gain, finalAmount)
                ledgerDao.insert(
                    ExpLedgerEntity(
                        ownerId = ownerId,
                        gainId = gain,
                        taskId = taskId,
                        amount = finalAmount,
                        reason = ExpReason.TASK_COMPLETE,
                    ),
                )
            }
            taskDao.updateTask(
                current.task.copy(
                    status = TaskStatus.DONE,
                    completedAt = now.toEpochMillis(),
                    expAwarded = awarded,
                    skorTerkunci = true,
                    updatedAt = now.toEpochMillis(),
                ),
            )
        }
    }

    override suspend fun cancelTask(taskId: String) {
        val current = taskDao.observeTask(taskId).first() ?: return
        if (current.task.status != TaskStatus.ACTIVE) return
        val now = LocalDateTime.now()
        taskDao.updateTask(
            current.task.copy(
                status = TaskStatus.CANCELLED,
                cancelledAt = now.toEpochMillis(),
                updatedAt = now.toEpochMillis(),
            ),
        )
    }

    private fun TaskWithDetails.toUi(): TaskUi = TaskUi(
        id = task.id,
        title = task.title,
        notes = task.notes,
        difficulty = task.difficulty,
        impact = task.impact,
        timeType = task.timeType,
        dueAt = task.dueAt?.toLocalDateTime(),
        estimasiMenit = task.estimasiMenit,
        status = task.status,
        completedAt = task.completedAt?.toLocalDateTime(),
        cancelledAt = task.cancelledAt?.toLocalDateTime(),
        expAwarded = task.expAwarded,
        subtasks = subtasks.sortedBy { it.position }.map { SubtaskUi(it.id, it.title, it.done, it.position) },
        allocations = allocations.associate { it.gainId to it.percent },
    )
}
