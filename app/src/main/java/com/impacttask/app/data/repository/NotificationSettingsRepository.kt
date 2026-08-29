package com.impacttask.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/** Minutes since midnight, e.g. 22:00 -> 1320. Avoids pulling in a LocalTime serializer. */
data class NotificationSettings(
    val quietStartMinute: Int = 22 * 60,
    val quietEndMinute: Int = 7 * 60,
    val dailyBudget: Int = 6,
    val allowHighTierDuringQuietHours: Boolean = false,
) {
    /** Handles the overnight wraparound (e.g. 22:00 -> 07:00 crosses midnight). */
    fun isQuietAt(minuteOfDay: Int): Boolean = if (quietStartMinute <= quietEndMinute) {
        minuteOfDay in quietStartMinute until quietEndMinute
    } else {
        minuteOfDay >= quietStartMinute || minuteOfDay < quietEndMinute
    }
}

private val Context.notificationDataStore by preferencesDataStore(name = "impact_task_notifications")

interface NotificationSettingsRepository {
    val settings: Flow<NotificationSettings>

    suspend fun setQuietHours(startMinute: Int, endMinute: Int)
    suspend fun setDailyBudget(budget: Int)
    suspend fun setAllowHighTierDuringQuietHours(allow: Boolean)

    /** Atomically checks and, if under budget, consumes one slot for today. */
    suspend fun tryConsumeBudget(): Boolean
}

@Singleton
class NotificationSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationSettingsRepository {

    override val settings: Flow<NotificationSettings> = context.notificationDataStore.data.map { prefs ->
        NotificationSettings(
            quietStartMinute = prefs[QUIET_START] ?: 22 * 60,
            quietEndMinute = prefs[QUIET_END] ?: 7 * 60,
            dailyBudget = prefs[DAILY_BUDGET] ?: 6,
            allowHighTierDuringQuietHours = (prefs[ALLOW_HIGH_TIER] ?: 0) == 1,
        )
    }

    override suspend fun setQuietHours(startMinute: Int, endMinute: Int) {
        context.notificationDataStore.edit { it[QUIET_START] = startMinute; it[QUIET_END] = endMinute }
    }

    override suspend fun setDailyBudget(budget: Int) {
        context.notificationDataStore.edit { it[DAILY_BUDGET] = budget.coerceIn(1, 6) }
    }

    override suspend fun setAllowHighTierDuringQuietHours(allow: Boolean) {
        context.notificationDataStore.edit { it[ALLOW_HIGH_TIER] = if (allow) 1 else 0 }
    }

    override suspend fun tryConsumeBudget(): Boolean {
        val today = LocalDate.now().toString()
        val budget = settings.first().dailyBudget
        var consumed = false
        context.notificationDataStore.edit { prefs ->
            val sameDay = prefs[SENT_DATE] == today
            val countSoFar = if (sameDay) prefs[SENT_COUNT] ?: 0 else 0
            if (countSoFar < budget) {
                prefs[SENT_DATE] = today
                prefs[SENT_COUNT] = countSoFar + 1
                consumed = true
            } else {
                prefs[SENT_DATE] = today
                prefs[SENT_COUNT] = countSoFar
            }
        }
        return consumed
    }

    private companion object {
        val QUIET_START = intPreferencesKey("quiet_start_minute")
        val QUIET_END = intPreferencesKey("quiet_end_minute")
        val DAILY_BUDGET = intPreferencesKey("daily_budget")
        val ALLOW_HIGH_TIER = intPreferencesKey("allow_high_tier_quiet")
        val SENT_DATE = stringPreferencesKey("sent_date")
        val SENT_COUNT = intPreferencesKey("sent_count")
    }
}
