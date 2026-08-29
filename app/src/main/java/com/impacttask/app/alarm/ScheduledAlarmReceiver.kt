package com.impacttask.app.alarm

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.data.repository.TaskRepository
import com.impacttask.app.domain.model.TaskUi
import com.impacttask.app.notification.NotificationChannels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fired by AlarmManager at a Terjadwal task's exact start time. Bypasses the
 * daily notification budget entirely -- prd.md section 06 treats this as a
 * genuine time-exact alarm (like a phone alarm clock), not a "disturbance".
 */
@AndroidEntryPoint
class ScheduledAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: return
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                val task = taskRepository.observeTask(taskId).first()
                if (task != null && task.status == TaskStatus.ACTIVE) {
                    postAlarmNotification(context, task)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun postAlarmNotification(context: Context, task: TaskUi) {
        val tierNumber = task.tier.ordinal + 1
        val useFullScreen = tierNumber >= 7

        val contentIntent = PendingIntent.getActivity(
            context,
            task.id.hashCode(),
            Intent(context, AlarmActivity::class.java)
                .putExtra(EXTRA_TASK_ID, task.id)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(context, NotificationChannels.TASK_ALARMS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Waktunya: ${task.title}")
            .setContentText("${task.tier.label} bangun tepat waktu.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)

        // prd.md section 06: full-screen intent only for Terjadwal tier 7+.
        if (useFullScreen) {
            builder.setFullScreenIntent(contentIntent, true)
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(task.id.hashCode(), builder.build())
        }
    }
}
