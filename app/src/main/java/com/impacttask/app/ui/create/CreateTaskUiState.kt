package com.impacttask.app.ui.create

import com.impacttask.Gain
import com.impacttask.TaskCalculator
import com.impacttask.ThreatTier
import com.impacttask.app.data.model.TaskTimeType
import com.impacttask.app.util.toLocalDateTime
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToInt

data class CreateTaskUiState(
    val title: String = "",
    val notes: String = "",
    val difficulty: Int = 50,
    val impact: Int = 50,
    val timeType: TaskTimeType = TaskTimeType.BERTENGGAT,
    /** Midnight-epoch millis for the chosen date, date-only (see [dueAt] for the combined value). */
    val dueDateMillis: Long? = null,
    val dueHour: Int = 18,
    val dueMinute: Int = 0,
    val estimasiMenit: Int = 30,
    val primaryGain: Gain = Gain.RAGA,
    val splitEnabled: Boolean = false,
    val secondaryGain: Gain? = null,
    val secondaryPercent: Int = 0,
    val tertiaryGain: Gain? = null,
    val tertiaryPercent: Int = 0,
) {
    val score: Int get() = (difficulty + impact).coerceIn(0, 200)
    val tier: ThreatTier get() = ThreatTier.fromScore(score)
    val baseExp: Int get() = TaskCalculator.baseExperience(difficulty, impact).roundToInt()

    val canSubmit: Boolean get() = title.isNotBlank()

    val dueAt: LocalDateTime?
        get() {
            if (timeType == TaskTimeType.LENTUR || dueDateMillis == null) return null
            val date = dueDateMillis.toLocalDateTime().toLocalDate()
            return LocalDateTime.of(date, LocalTime.of(dueHour, dueMinute))
        }

    /** Primary gets whatever percent isn't claimed by the (optional) secondary/tertiary split. */
    val allocations: Map<Gain, Int>
        get() {
            val secondary = if (splitEnabled && secondaryGain != null && secondaryPercent > 0) secondaryPercent else 0
            val tertiary = if (splitEnabled && tertiaryGain != null && tertiaryPercent > 0) tertiaryPercent else 0
            val used = secondary + tertiary
            val result = linkedMapOf(primaryGain to (100 - used).coerceIn(10, 100))
            if (secondary > 0) result[secondaryGain!!] = secondary
            if (tertiary > 0) result[tertiaryGain!!] = tertiary
            return result
        }
}
