package com.impacttask.app.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.impacttask.app.data.repository.TaskRepository
import com.impacttask.app.domain.model.TaskUi
import com.impacttask.app.ui.nav.ImpactDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle[ImpactDestination.TaskDetail.ARG_TASK_ID])

    val task: StateFlow<TaskUi?> = taskRepository.observeTask(taskId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addSubtask(title: String) {
        viewModelScope.launch { taskRepository.addSubtask(taskId, title) }
    }

    fun setSubtaskDone(subtaskId: String, done: Boolean) {
        viewModelScope.launch { taskRepository.setSubtaskDone(taskId, subtaskId, done) }
    }

    fun complete() {
        viewModelScope.launch { taskRepository.completeTask(taskId) }
    }

    fun cancel() {
        viewModelScope.launch { taskRepository.cancelTask(taskId) }
    }
}
