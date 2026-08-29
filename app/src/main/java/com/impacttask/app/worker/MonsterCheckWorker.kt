package com.impacttask.app.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.impacttask.MonsterState
import com.impacttask.ThreatTier
import com.impacttask.app.MainActivity
import com.impacttask.app.data.db.MonsterDao
import com.impacttask.app.data.db.entity.MonsterEntity
import com.impacttask.app.data.identity.OwnerIdProvider
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.data.model.TaskTimeType
import com.impacttask.app.data.repository.NotificationSettingsRepository
import com.impacttask.app.data.repository.TaskRepository
import com.impacttask.app.domain.model.TaskUi
import com.impacttask.app.notification.NotificationChannels
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Periodic check for monster state transitions (Stirring for tier 7+, Rampage,
 * Feral) on non-Terjadwal active tasks, subject to the daily notification
 * budget and quiet hours (prd.md section 06). Terjadwal tasks use exact
 * AlarmManager alarms instead (see the .alarm package), not this worker.
 *
 * Scope note: the per-tier "perilaku saat menunggak" table in prd.md section
 * 05 also describes home-screen visual effects (screen shake, desaturation,
 * EXP decay, a monster walking across the screen) -- those belong to the
 * Beranda screen once it exists (Fase 3/4), not to this notification worker.
 */
@HiltWorker
class MonsterCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val monsterDao: MonsterDao,
    private val notificationSettingsRepository: NotificationSettingsRepository,
    private val ownerIdProvider: OwnerIdProvider,
) : CoroutineWorker(context, workerParams) {

    private data class Candidate(val task: TaskUi, val tier: ThreatTier, val state: MonsterState)

    override suspend fun doWork(): Result {
        // ownerId is resolved for parity with the rest of the data layer, even
        // though this worker currently operates on the single local owner.
        ownerIdProvider.getOwnerId()

        val activeTasks = taskRepository.observeTasks().first()
            .filter { it.status == TaskStatus.ACTIVE && it.timeType != TaskTimeType.TERJADWAL }

        val settings = notificationSettingsRepository.settings.first()
        val nowMinute = LocalTime.now().let { it.hour * 60 + it.minute }
        val quietNow = settings.isQuietAt(nowMinute)

        val candidates = activeTasks.mapNotNull { task ->
            val tier = task.tier
            val state = task.state
            val tierNumber = tier.ordinal + 1
            val notifyWorthy = when (state) {
                MonsterState.STIRRING -> tierNumber >= 7
                MonsterState.RAMPAGE, MonsterState.FERAL -> true
                else -> false
            }
            if (!notifyWorthy) return@mapNotNull null

            val monster = monsterDao.get(task.id)
            if (monster?.lastNotifiedState == state) return@mapNotNull null

            Candidate(task, tier, state)
        }.sortedByDescending { it.tier.ordinal }

        if (candidates.isEmpty()) return Result.success()

        val allowedNow = candidates.filter { candidate ->
            val tierNumber = candidate.tier.ordinal + 1
            !quietNow || (tierNumber >= 9 && settings.allowHighTierDuringQuietHours)
        }
        if (allowedNow.isEmpty()) return Result.success()

        val leftover = mutableListOf<Candidate>()
        for (candidate in allowedNow) {
            if (notificationSettingsRepository.tryConsumeBudget()) {
                postTaskNotification(candidate.task, candidate.tier, candidate.state)
                val previousCount = monsterDao.get(candidate.task.id)?.disturbanceCount ?: 0
                monsterDao.upsert(
                    MonsterEntity(
                        taskId = candidate.task.id,
                        lastNotifiedState = candidate.state,
                        lastNotifiedAt = System.currentTimeMillis(),
                        disturbanceCount = previousCount + 1,
                    ),
                )
            } else {
                leftover.add(candidate)
            }
        }

        if (leftover.isNotEmpty()) {
            postSummaryNotification(leftover.size)
        }

        return Result.success()
    }

    private fun postTaskNotification(task: TaskUi, tier: ThreatTier, state: MonsterState) {
        if (!hasNotificationPermission()) return

        val stateLabel = when (state) {
            MonsterState.STIRRING -> "mulai menggeliat"
            MonsterState.RAMPAGE -> "mengamuk"
            MonsterState.FERAL -> "jadi liar"
            else -> "butuh perhatian"
        }

        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.TASK_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("${tier.label} ${stateLabel}")
            .setContentText(task.title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent())
            .build()

        NotificationManagerCompat.from(applicationContext).notify(task.id.hashCode(), notification)
    }

    private fun postSummaryNotification(extraCount: Int) {
        if (!hasNotificationPermission()) return

        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.TASK_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Jatah notifikasi hari ini penuh")
            .setContentText("$extraCount monster lain sedang menunggu.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent())
            .build()

        NotificationManagerCompat.from(applicationContext).notify(SUMMARY_NOTIFICATION_ID, notification)
    }

    private fun openAppPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun hasNotificationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "monster-check"
        private const val SUMMARY_NOTIFICATION_ID = -1

        fun schedulePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<MonsterCheckWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
