package com.impacttask.app.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.impacttask.app.domain.model.TaskUi
import com.impacttask.app.util.toEpochMillis
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

const val EXTRA_TASK_ID = "task_id"

/**
 * Exact-time alarms for Terjadwal tasks (prd.md section 08). AlarmManager
 * alarms are cleared on reboot, so BootReceiver re-schedules every active
 * Terjadwal task's alarm on boot -- see that class.
 */
@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun scheduleFor(task: TaskUi) {
        val dueAt = task.dueAt ?: return
        val triggerAt = dueAt.toEpochMillis()
        if (triggerAt <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService<AlarmManager>() ?: return
        if (!canScheduleExactAlarms()) return

        val pendingIntent = alarmPendingIntent(task.id)
        val showIntent = PendingIntent.getActivity(
            context,
            task.id.hashCode(),
            Intent(context, AlarmActivity::class.java)
                .putExtra(EXTRA_TASK_ID, task.id)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerAt, showIntent),
            pendingIntent,
        )
    }

    fun cancelFor(taskId: String) {
        val alarmManager = context.getSystemService<AlarmManager>() ?: return
        alarmManager.cancel(alarmPendingIntent(taskId))
    }

    private fun alarmPendingIntent(taskId: String): PendingIntent {
        val intent = Intent(context, ScheduledAlarmReceiver::class.java)
            .putExtra(EXTRA_TASK_ID, taskId)
        return PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** True on API < 31 (no such restriction existed yet) and once granted on 31+. */
    fun canScheduleExactAlarms(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService<AlarmManager>() ?: return false
        return alarmManager.canScheduleExactAlarms()
    }
}
