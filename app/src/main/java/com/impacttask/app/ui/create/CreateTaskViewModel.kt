package com.impacttask.app.ui.create

import androidx.lifecycle.ViewModel
import com.impacttask.Gain
import com.impacttask.app.data.model.TaskTimeType
import com.impacttask.app.data.repository.TaskRepository
import com.impacttask.app.domain.model.NewTaskInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CreateTaskUiState())
    val state: StateFlow<CreateTaskUiState> = _state.asStateFlow()

    fun setTitle(value: String) = _state.update { it.copy(title = value) }
    fun setNotes(value: String) = _state.update { it.copy(notes = value) }
    fun setDifficulty(value: Int) = _state.update { it.copy(difficulty = value) }
    fun setImpact(value: Int) = _state.update { it.copy(impact = value) }
    fun setTimeType(value: TaskTimeType) = _state.update { it.copy(timeType = value) }
    fun setDueDateMillis(value: Long) = _state.update { it.copy(dueDateMillis = value) }
    fun setDueTime(hour: Int, minute: Int) = _state.update { it.copy(dueHour = hour, dueMinute = minute) }
    fun setEstimasiMenit(value: Int) = _state.update { it.copy(estimasiMenit = value) }

    fun setPrimaryGain(gain: Gain) = _state.update {
        it.copy(
            primaryGain = gain,
            secondaryGain = it.secondaryGain?.takeIf { g -> g != gain },
            tertiaryGain = it.tertiaryGain?.takeIf { g -> g != gain },
        )
    }

    fun toggleSplit() = _state.update { it.copy(splitEnabled = !it.splitEnabled) }
    fun setSecondaryGain(gain: Gain?) = _state.update { it.copy(secondaryGain = gain) }
    fun setSecondaryPercent(value: Int) = _state.update { it.copy(secondaryPercent = value) }
    fun setTertiaryGain(gain: Gain?) = _state.update { it.copy(tertiaryGain = gain) }
    fun setTertiaryPercent(value: Int) = _state.update { it.copy(tertiaryPercent = value) }

    /** Returns the new task's id, or null if the form isn't valid yet. */
    suspend fun submit(): String? {
        val s = _state.value
        if (!s.canSubmit) return null
        return taskRepository.createTask(
            NewTaskInput(
                title = s.title.trim(),
                notes = s.notes.trim(),
                difficulty = s.difficulty,
                impact = s.impact,
                timeType = s.timeType,
                dueAt = s.dueAt,
                estimasiMenit = s.estimasiMenit,
                allocations = s.allocations,
            ),
        )
    }
}
