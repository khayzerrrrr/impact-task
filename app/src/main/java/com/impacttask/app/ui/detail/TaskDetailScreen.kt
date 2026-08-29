package com.impacttask.app.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.impacttask.TaskCalculator
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.domain.model.TaskUi
import com.impacttask.app.ui.components.color
import com.impacttask.app.ui.components.label
import com.impacttask.app.ui.components.number
import com.impacttask.app.ui.components.tone
import com.impacttask.app.util.formatDue
import java.time.LocalDateTime
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    onBack: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel(),
) {
    val task by viewModel.task.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(task?.title ?: "Task detail", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        val current = task
        if (current == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Memuat...")
            }
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(padding),
        ) {
            item { TaskHeroCard(current) }

            if (current.notes.isNotBlank()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Catatan", style = MaterialTheme.typography.labelSmall)
                            Text(current.notes)
                        }
                    }
                }
            }

            item {
                SubtaskCard(
                    task = current,
                    onToggle = { id, done -> viewModel.setSubtaskDone(id, done) },
                    onAdd = viewModel::addSubtask,
                    onComplete = viewModel::complete,
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Gain allocation", style = MaterialTheme.typography.labelSmall)
                        current.allocations.entries.sortedByDescending { it.value }.forEach { (gain, pct) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(gain.label, color = gain.color())
                                Text("$pct%", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            if (current.status == TaskStatus.ACTIVE) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Button(onClick = viewModel::complete, modifier = Modifier.weight(2f)) {
                            Text("Tandai selesai")
                        }
                        OutlinedButton(onClick = viewModel::cancel, modifier = Modifier.weight(1f)) {
                            Text("Batalkan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskHeroCard(task: TaskUi) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Tier ${task.tier.number()} · ${task.tier.label}", style = MaterialTheme.typography.titleLarge)

            val statusText = when (task.status) {
                TaskStatus.ACTIVE -> task.state.label()
                TaskStatus.DONE -> "Dikalahkan"
                TaskStatus.CANCELLED -> "Dilepaskan"
            }
            val statusColor = if (task.status == TaskStatus.ACTIVE) task.state.tone() else MaterialTheme.colorScheme.onSurfaceVariant
            Text(statusText, color = statusColor, fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MetricColumn(label = "Due", value = if (task.status == TaskStatus.ACTIVE) formatDue(task.dueAt) else "-")
                MetricColumn(label = "EXP", value = (task.expAwarded ?: task.previewExp()).toString())
                MetricColumn(label = "HP", value = "${task.hpPercent}%")
            }
        }
    }
}

/** EXP an active task would award if completed right now (base * timing; no streak/balance yet -- those depend on account-wide state resolved at completion time). */
private fun TaskUi.previewExp(): Int {
    val base = TaskCalculator.baseExperience(difficulty, impact)
    val multiplier = TaskCalculator.timeMultiplier(dueAt, LocalDateTime.now())
    return (base * multiplier).roundToInt()
}

@Composable
private fun MetricColumn(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SubtaskCard(
    task: TaskUi,
    onToggle: (String, Boolean) -> Unit,
    onAdd: (String) -> Unit,
    onComplete: () -> Unit,
) {
    var newSubtask by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Subtask", style = MaterialTheme.typography.labelSmall)

            if (task.subtasks.isEmpty()) {
                Text("Belum ada subtask.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                task.subtasks.sortedBy { it.position }.forEach { subtask ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = subtask.done,
                            onCheckedChange = { checked -> onToggle(subtask.id, checked) },
                            enabled = task.status == TaskStatus.ACTIVE,
                        )
                        Text(subtask.title)
                    }
                }
            }

            if (task.status == TaskStatus.ACTIVE) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newSubtask,
                        onValueChange = { newSubtask = it },
                        label = { Text("Tambah langkah...") },
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = {
                        if (newSubtask.isNotBlank()) {
                            onAdd(newSubtask)
                            newSubtask = ""
                        }
                    }) {
                        Text("+")
                    }
                }
            }

            if (task.status == TaskStatus.ACTIVE && task.allSubtasksDone) {
                OutlinedButton(onClick = onComplete, modifier = Modifier.fillMaxWidth()) {
                    Text("Semua langkah selesai -- tandai task ini selesai?")
                }
            }
        }
    }
}
