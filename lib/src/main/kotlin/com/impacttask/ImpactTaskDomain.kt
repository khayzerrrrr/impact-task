package com.impacttask

import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToInt

enum class Gain(val label: String) {
    RAGA("Raga"),
    NALAR("Nalar"),
    KARYA("Karya"),
    HARTA("Harta"),
    IKATAN("Ikatan"),
    JIWA("Jiwa");
}

enum class ThreatTier(val minScore: Int, val maxScore: Int, val label: String) {
    SPECK(0, 19, "Speck"),
    BLOB(20, 39, "Blob"),
    IMP(40, 59, "Imp"),
    GOBLIN(60, 79, "Goblin"),
    BRUTE(80, 99, "Brute"),
    STALKER(100, 119, "Stalker"),
    WARDEN(120, 139, "Warden"),
    BEHEMOTH(140, 159, "Behemoth"),
    WRAITH(160, 179, "Wraith"),
    TITAN(180, 200, "Titan");

    companion object {
        fun fromScore(score: Int): ThreatTier {
            val clamped = score.coerceIn(0, 200)
            val tierIndex = (clamped / 20) + 1
            return entries[(tierIndex - 1).coerceIn(0, entries.size - 1)]
        }
    }
}

enum class MonsterState(val priorityWeight: Int) {
    DORMANT(1),
    STIRRING(2),
    AWAKE(3),
    RAMPAGE(4),
    FERAL(5);

    companion object {
        fun fromDeadline(deadline: LocalDateTime?, now: LocalDateTime): MonsterState {
            if (deadline == null) return DORMANT

            val remainingHours = Duration.between(now, deadline).toHours()
            return when {
                remainingHours > 72 -> DORMANT
                remainingHours > 24 -> STIRRING
                remainingHours >= 0 -> AWAKE
                remainingHours > -72 -> RAMPAGE
                else -> FERAL
            }
        }
    }
}

data class LevelProgress(val level: Int, val floorExp: Int, val ceilExp: Int, val progress: Double)

object TaskCalculator {
    fun baseExperience(difficulty: Int, impact: Int): Double {
        return (impact * 1.0) + (difficulty * 0.7)
    }

    /**
     * Completed >=24h early -> 1.15, on time -> 1.00, no deadline -> 1.00,
     * late -> decays 0.05/day, floored at 0.40 (never zero: effort still counts).
     */
    fun timeMultiplier(deadline: LocalDateTime?, now: LocalDateTime): Double {
        if (deadline == null) return 1.0
        val remainingHours = Duration.between(now, deadline).toHours()
        return when {
            remainingHours >= 24 -> 1.15
            remainingHours >= 0 -> 1.0
            else -> {
                val lateDays = Math.ceil(-remainingHours / 24.0)
                maxOf(0.40, 1 - 0.05 * lateDays)
            }
        }
    }

    fun streakMultiplier(streakDays: Int): Double {
        return 1 + minOf(0.20, 0.02 * streakDays)
    }

    fun balanceMultiplier(targetGain: Gain, gainLevels: Map<Gain, Int>): Double {
        val lowestLevel = gainLevels.values.minOrNull() ?: return 1.0
        val isLowest = (gainLevels[targetGain] ?: lowestLevel) == lowestLevel
        return if (isLowest) 1.15 else 1.0
    }

    fun distributeExperience(total: Int, allocations: Map<Gain, Int>): Map<Gain, Int> {
        val normalized = allocations.filterValues { it > 0 }
        if (normalized.isEmpty()) return emptyMap()

        val totalAllocation = normalized.values.sum()
        return normalized.mapValues { (_, percent) ->
            ((total * percent.toDouble()) / totalAllocation.toDouble()).roundToInt()
        }
    }

    /** Cumulative EXP required to reach level n. lv2 -> 303, lv10 -> 3981 (see PRD 07). */
    fun expForLevel(level: Int): Int {
        if (level <= 0) return 0
        return (100 * Math.pow(level.toDouble(), 1.6)).roundToInt()
    }

    fun levelProgress(totalExp: Int): LevelProgress {
        var level = 0
        while (level < 200 && expForLevel(level + 1) <= totalExp) level++
        val floorExp = expForLevel(level)
        val ceilExp = expForLevel(level + 1)
        val progress = if (ceilExp > floorExp) {
            (totalExp - floorExp).toDouble() / (ceilExp - floorExp)
        } else {
            0.0
        }
        return LevelProgress(level, floorExp, ceilExp, progress.coerceIn(0.0, 1.0))
    }

    /**
     * Auto-priority ordering (PRD 12): monster state dominates tier, since a
     * rampaging light task outranks a heavy but still-dormant one.
     */
    fun priorityScore(tier: ThreatTier, state: MonsterState): Int {
        return state.priorityWeight * 1000 + (tier.ordinal + 1) * 10
    }
}
