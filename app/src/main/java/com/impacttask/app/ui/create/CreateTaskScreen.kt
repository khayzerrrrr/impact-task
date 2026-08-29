package com.impacttask.app.ui.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.impacttask.Gain
import com.impacttask.app.data.model.TaskTimeType
import com.impacttask.app.ui.components.glyph
import com.impacttask.app.ui.components.number
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onBack: () -> Unit,
    onCreated: (String) -> Unit,
    viewModel: CreateTaskViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(padding),
        ) {
            item {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::setTitle,
                    label = { Text("Judul task") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = viewModel::setNotes,
                    label = { Text("Catatan (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Preview monster", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "Tier ${state.tier.number()} · ${state.tier.label}",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text("Score ${state.score} · EXP dasar ${state.baseExp}")

                        Text("Kesulitan: ${state.difficulty}")
                        Slider(
                            value = state.difficulty.toFloat(),
                            onValueChange = { viewModel.setDifficulty(it.toInt()) },
                            valueRange = 0f..100f,
                        )
                        Text("Dampak: ${state.impact}")
                        Slider(
                            value = state.impact.toFloat(),
                            onValueChange = { viewModel.setImpact(it.toInt()) },
                            valueRange = 0f..100f,
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Jenis waktu", style = MaterialTheme.typography.labelSmall)
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        val types = TaskTimeType.entries.toList()
                        types.forEachIndexed { index, type ->
                            SegmentedButton(
                                selected = state.timeType == type,
                                onClick = { viewModel.setTimeType(type) },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = types.size),
                            ) {
                                Text(
                                    when (type) {
                                        TaskTimeType.LENTUR -> "Lentur"
                                        TaskTimeType.BERTENGGAT -> "Bertenggat"
                                        TaskTimeType.TERJADWAL -> "Terjadwal"
                                    },
                                )
                            }
                        }
                    }
                }
            }

            if (state.timeType != TaskTimeType.LENTUR) {
                item {
                    val calendar = remember { Calendar.getInstance() }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    calendar.set(year, month, day, 0, 0, 0)
                                    viewModel.setDueDateMillis(calendar.timeInMillis)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                            ).show()
                        }) {
                            Text(if (state.dueDateMillis == null) "Pilih tanggal" else "Ganti tanggal")
                        }
                        TextButton(onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute -> viewModel.setDueTime(hour, minute) },
                                state.dueHour,
                                state.dueMinute,
                                true,
                            ).show()
                        }) {
                            Text("Jam %02d:%02d".format(state.dueHour, state.dueMinute))
                        }
                    }
                }
            } else {
                item {
                    OutlinedTextField(
                        value = state.estimasiMenit.toString(),
                        onValueChange = { it.toIntOrNull()?.let(viewModel::setEstimasiMenit) },
                        label = { Text("Estimasi durasi (menit)") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Primary Gain", style = MaterialTheme.typography.labelSmall)
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Gain.entries.toList().chunked(3).forEach { rowGains ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                rowGains.forEach { gain ->
                                    GainChip(
                                        gain = gain,
                                        selected = state.primaryGain == gain,
                                        onClick = { viewModel.setPrimaryGain(gain) },
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Bagi ke Gains lain")
                        Switch(checked = state.splitEnabled, onCheckedChange = { viewModel.toggleSplit() })
                    }
                }
            }

            if (state.splitEnabled) {
                item {
                    SplitGainRow(
                        label = "Gain kedua",
                        options = Gain.entries.filter { it != state.primaryGain && it != state.tertiaryGain },
                        selected = state.secondaryGain,
                        percent = state.secondaryPercent,
                        maxPercent = 40,
                        onGainSelected = viewModel::setSecondaryGain,
                        onPercentChanged = viewModel::setSecondaryPercent,
                    )
                }
                item {
                    SplitGainRow(
                        label = "Gain ketiga",
                        options = Gain.entries.filter { it != state.primaryGain && it != state.secondaryGain },
                        selected = state.tertiaryGain,
                        percent = state.tertiaryPercent,
                        maxPercent = 30,
                        onGainSelected = viewModel::setTertiaryGain,
                        onPercentChanged = viewModel::setTertiaryPercent,
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.submit()?.let(onCreated)
                        }
                    },
                    enabled = state.canSubmit,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Buat task")
                }
            }
        }
    }
}

@Composable
private fun GainChip(gain: Gain, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text("${gain.glyph()} ${gain.label}") },
        modifier = Modifier.padding(4.dp),
    )
}

@Composable
private fun SplitGainRow(
    label: String,
    options: List<Gain>,
    selected: Gain?,
    percent: Int,
    maxPercent: Int,
    onGainSelected: (Gain?) -> Unit,
    onPercentChanged: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$label${if (selected != null) " · $percent%" else ""}", style = MaterialTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(
                selected = selected == null,
                onClick = { onGainSelected(null) },
                label = { Text("Tidak ada") },
            )
            options.forEach { gain ->
                FilterChip(
                    selected = selected == gain,
                    onClick = { onGainSelected(gain) },
                    label = { Text(gain.label) },
                )
            }
        }
        if (selected != null) {
            Slider(
                value = percent.toFloat(),
                onValueChange = { onPercentChanged(it.toInt()) },
                valueRange = 0f..maxPercent.toFloat(),
            )
        }
    }
}
