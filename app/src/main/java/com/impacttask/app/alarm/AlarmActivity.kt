package com.impacttask.app.alarm

import android.app.KeyguardManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.impacttask.app.data.repository.TaskRepository
import com.impacttask.app.domain.model.TaskUi
import com.impacttask.app.ui.theme.ImpactTaskTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Full-screen alarm shown for Terjadwal tasks (tier 7+) at their exact start time. */
@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        getSystemService<KeyguardManager>()?.requestDismissKeyguard(this, null)

        val taskId = intent.getStringExtra(EXTRA_TASK_ID)

        setContent {
            ImpactTaskTheme {
                val task by produceState<TaskUi?>(initialValue = null, taskId) {
                    if (taskId != null) {
                        taskRepository.observeTask(taskId).collect { value = it }
                    }
                }
                AlarmScreenContent(task = task, onDismiss = ::finish)
            }
        }
    }
}

@Composable
private fun AlarmScreenContent(task: TaskUi?, onDismiss: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Waktunya!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            Text(
                task?.title ?: "Task terjadwal",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            if (task != null) {
                Spacer(Modifier.height(6.dp))
                Text("Tier ${task.tier.ordinal + 1} · ${task.tier.label}")
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Oke, aku kerjakan")
            }
        }
    }
}
