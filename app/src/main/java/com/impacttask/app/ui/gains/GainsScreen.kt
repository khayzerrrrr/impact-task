package com.impacttask.app.ui.gains

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.impacttask.app.domain.model.GainProgress
import com.impacttask.app.ui.components.color
import com.impacttask.app.ui.components.subtitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GainsScreen(viewModel: GainsViewModel = hiltViewModel()) {
    val gains by viewModel.gains.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gains") }) },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().padding(padding),
        ) {
            val neglected = gains.minByOrNull { it.level }
            if (neglected != null) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "${neglected.gain.label} paling tertinggal -- task ke sana dapat bonus +15% EXP.",
                            modifier = Modifier.padding(12.dp),
                            color = neglected.gain.color(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            items(gains, key = { it.gain.name }) { progress ->
                GainRow(progress)
            }
        }
    }
}

@Composable
private fun GainRow(progress: GainProgress) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(progress.gain.label, fontWeight = FontWeight.SemiBold, color = progress.gain.color())
                    Text(progress.gain.subtitle(), style = MaterialTheme.typography.bodyMedium)
                }
                Text("Level ${progress.level}", fontWeight = FontWeight.Bold)
            }

            val fraction = progress.progress.toFloat().coerceIn(0f, 1f)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(8.dp)
                        .background(progress.gain.color(), RoundedCornerShape(4.dp)),
                ) {}
            }

            Text(
                "${progress.totalExp} EXP · ${progress.ceilExp} EXP untuk lv ${progress.level + 1}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
