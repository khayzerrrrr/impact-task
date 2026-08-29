package com.impacttask.app.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.data.model.TaskTimeType
import com.impacttask.app.data.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** AlarmManager alarms are cleared on reboot (prd.md section 08); this puts them back. */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                taskRepository.observeTasks().first()
                    .filter { it.status == TaskStatus.ACTIVE && it.timeType == TaskTimeType.TERJADWAL && it.dueAt != null }
                    .forEach(alarmScheduler::scheduleFor)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
