package com.impacttask.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.impacttask.app.alarm.AlarmScheduler
import com.impacttask.app.data.repository.NotificationSettings
import com.impacttask.app.data.repository.NotificationSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val notificationSettingsRepository: NotificationSettingsRepository,
    private val alarmScheduler: AlarmScheduler,
) : ViewModel() {

    val settings: StateFlow<NotificationSettings> = notificationSettingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationSettings())

    fun setQuietHours(startMinute: Int, endMinute: Int) {
        viewModelScope.launch { notificationSettingsRepository.setQuietHours(startMinute, endMinute) }
    }

    fun setDailyBudget(budget: Int) {
        viewModelScope.launch { notificationSettingsRepository.setDailyBudget(budget) }
    }

    fun setAllowHighTierDuringQuietHours(allow: Boolean) {
        viewModelScope.launch { notificationSettingsRepository.setAllowHighTierDuringQuietHours(allow) }
    }

    fun canScheduleExactAlarms(): Boolean = alarmScheduler.canScheduleExactAlarms()
}
