package com.impacttask.app.focus

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.impacttask.app.notification.NotificationChannels

/**
 * Ongoing notification while a task's focus session runs (prd.md: "Tombol
 * Mulai di detail task menjalankan timer di notifikasi permanen"). The
 * notification itself, via setUsesChronometer, is the timer -- no separate
 * ticking logic needed.
 */
class FocusSessionService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            @Suppress("DEPRECATION")
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, FocusSessionService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, NotificationChannels.FOCUS_SESSION)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Sesi fokus berjalan")
            .setContentText("Monster terkurung selama kamu fokus.")
            .setOngoing(true)
            .setUsesChronometer(true)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(0, "Selesai", stopIntent)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 9001
        private const val ACTION_STOP = "com.impacttask.app.focus.STOP"

        fun start(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, FocusSessionService::class.java))
        }

        fun stop(context: Context) {
            context.startService(
                Intent(context, FocusSessionService::class.java).setAction(ACTION_STOP),
            )
        }
    }
}
