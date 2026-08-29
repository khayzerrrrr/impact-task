package com.impacttask

import java.time.Duration
import java.time.LocalDateTime

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

enum class MonsterState {
    DORMANT,
    STIRRING,
    AWAKE,
    RAMPAGE,
    FERAL;

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

object TaskCalculator {
    fun baseExperience(difficulty: Int, impact: Int): Double {
        return (impact * 1.0) + (difficulty * 0.7)
    }

    fun balanceMultiplier(gainLevels: Map<Gain, Int>): Double {
        val lowestLevel = gainLevels.values.minOrNull() ?: 0
        return if (lowestLevel <= 0) 1.0 else 1.15
    }

    fun distributeExperience(total: Int, allocations: Map<Gain, Int>): Map<Gain, Int> {
        val normalized = allocations.filterValues { it > 0 }
        if (normalized.isEmpty()) return emptyMap()

        val totalAllocation = normalized.values.sum()
        return normalized.mapValues { (gain, percent) ->
            ((total * percent.toDouble()) / totalAllocation.toDouble()).toInt().also {
                require(gain in Gain.entries)
            }
        }
    }
}
