package com.impacttask.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService

object NotificationChannels {
    const val TASK_REMINDERS = "task_reminders"
    const val TASK_ALARMS = "task_alarms"
    const val FOCUS_SESSION = "focus_session"

    fun createAll(context: Context) {
        val manager = context.getSystemService<NotificationManager>() ?: return

        manager.createNotificationChannel(
            NotificationChannel(
                TASK_REMINDERS,
                "Pengingat monster",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Monster yang menggeliat, bangun, mengamuk, atau liar."
            },
        )

        manager.createNotificationChannel(
            NotificationChannel(
                TASK_ALARMS,
                "Alarm task terjadwal",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Alarm tepat waktu untuk task Terjadwal, tier 7 ke atas."
            },
        )

        manager.createNotificationChannel(
            NotificationChannel(
                FOCUS_SESSION,
                "Sesi fokus",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Notifikasi permanen selama timer sesi fokus berjalan."
            },
        )
    }
}
