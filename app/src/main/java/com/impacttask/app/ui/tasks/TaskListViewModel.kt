package com.impacttask.app.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.data.repository.TaskRepository
import com.impacttask.app.domain.model.TaskUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    taskRepository: TaskRepository,
) : ViewModel() {

    val tasks: StateFlow<List<TaskUi>> = taskRepository.observeTasks()
        .map { list ->
            list.filter { it.status == TaskStatus.ACTIVE }
                .sortedWith(
                    compareByDescending<TaskUi> { it.priorityScore }
                        .thenBy(nullsLast<LocalDateTime>()) { it.dueAt },
                )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
