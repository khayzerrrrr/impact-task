package com.impacttask

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ImpactTaskDomainTest {
    @Test
    fun `threat tier maps difficulty and impact to monster grade`() {
        assertEquals(ThreatTier.SPECK, ThreatTier.fromScore(0))
        assertEquals(ThreatTier.IMP, ThreatTier.fromScore(50))
        assertEquals(ThreatTier.BRUTE, ThreatTier.fromScore(90))
        assertEquals(ThreatTier.TITAN, ThreatTier.fromScore(200))
    }

    @Test
    fun `base exp rewards impact more than difficulty`() {
        val baseExp = TaskCalculator.baseExperience(30, 90)
        assertEquals(111.0, baseExp, 0.0001)
    }

    @Test
    fun `exp is split across gains according to allocation`() {
        val allocations = mapOf(
            Gain.RAGA to 80,
            Gain.NALAR to 20,
        )

        val expByGain = TaskCalculator.distributeExperience(1000, allocations)
        assertEquals(800, expByGain[Gain.RAGA])
        assertEquals(200, expByGain[Gain.NALAR])
    }

    @Test
    fun `monster state moves through lifecycle based on deadline proximity`() {
        val now = LocalDateTime.of(2026, 8, 10, 9, 0)

        assertEquals(MonsterState.DORMANT, MonsterState.fromDeadline(now.plusDays(5), now))
        assertEquals(MonsterState.STIRRING, MonsterState.fromDeadline(now.plusDays(2), now))
        assertEquals(MonsterState.AWAKE, MonsterState.fromDeadline(now.plusDays(0), now))
        assertEquals(MonsterState.RAMPAGE, MonsterState.fromDeadline(now.minusDays(1), now))
        assertEquals(MonsterState.FERAL, MonsterState.fromDeadline(now.minusDays(5), now))
    }

    @Test
    fun `gain balancing bonus rewards only the lowest progress gain`() {
        val gainLevels = mapOf(
            Gain.RAGA to 12,
            Gain.NALAR to 8,
            Gain.KARYA to 18,
            Gain.HARTA to 9,
            Gain.IKATAN to 7,
            Gain.JIWA to 14,
        )

        val lowestGainMultiplier = TaskCalculator.balanceMultiplier(Gain.IKATAN, gainLevels)
        val otherGainMultiplier = TaskCalculator.balanceMultiplier(Gain.KARYA, gainLevels)

        assertTrue(lowestGainMultiplier > otherGainMultiplier)
        assertEquals(1.15, lowestGainMultiplier, 0.0001)
        assertEquals(1.00, otherGainMultiplier, 0.0001)
    }

    @Test
    fun `exp distribution rounds shares instead of truncating them`() {
        val allocations = mapOf(
            Gain.RAGA to 1,
            Gain.NALAR to 1,
        )

        // 7 * (1/2) = 3.5 per gain: round() gives 4, a naive truncation would give 3.
        val expByGain = TaskCalculator.distributeExperience(7, allocations)

        assertEquals(4, expByGain[Gain.RAGA])
        assertEquals(4, expByGain[Gain.NALAR])
    }

    @Test
    fun `time multiplier rewards early completion and decays with lateness`() {
        val now = LocalDateTime.of(2026, 8, 10, 9, 0)

        assertEquals(1.0, TaskCalculator.timeMultiplier(null, now), 0.0001)
        assertEquals(1.15, TaskCalculator.timeMultiplier(now.plusDays(2), now), 0.0001)
        assertEquals(1.0, TaskCalculator.timeMultiplier(now.plusHours(2), now), 0.0001)
        assertEquals(0.95, TaskCalculator.timeMultiplier(now.minusDays(1), now), 0.0001)
        // 20 days late would drop to 0.0, but the floor keeps effort worth something.
        assertEquals(0.40, TaskCalculator.timeMultiplier(now.minusDays(20), now), 0.0001)
    }

    @Test
    fun `streak multiplier caps at 20 percent after 10 days`() {
        assertEquals(1.0, TaskCalculator.streakMultiplier(0), 0.0001)
        assertEquals(1.10, TaskCalculator.streakMultiplier(5), 0.0001)
        assertEquals(1.20, TaskCalculator.streakMultiplier(10), 0.0001)
        assertEquals(1.20, TaskCalculator.streakMultiplier(40), 0.0001)
    }

    @Test
    fun `level curve matches the PRD reference points`() {
        assertEquals(303, TaskCalculator.expForLevel(2))
        assertEquals(3981, TaskCalculator.expForLevel(10))
        assertEquals(12068, TaskCalculator.expForLevel(20))

        // expForLevel(6) = 1758, expForLevel(7) = 2250, so 1800 EXP sits in level 6.
        val progress = TaskCalculator.levelProgress(1800)
        assertEquals(6, progress.level)
    }

    @Test
    fun `priority score matches the PRD worked example (section 12)`() {
        // Bayar tagihan listrik: Tier 2, Mengamuk (Rampage) -> 4020, ranks 1st.
        assertEquals(4020, TaskCalculator.priorityScore(ThreatTier.BLOB, MonsterState.RAMPAGE))
        // Presentasi klien besok: Tier 7, Bangun (Awake) -> 3070, ranks 2nd.
        assertEquals(3070, TaskCalculator.priorityScore(ThreatTier.WARDEN, MonsterState.AWAKE))
        // Servis motor: Tier 4, Menggeliat (Stirring) -> 2040, ranks 3rd.
        assertEquals(2040, TaskCalculator.priorityScore(ThreatTier.GOBLIN, MonsterState.STIRRING))
        // Belajar bahasa Jepang: Tier 10, Tidur (Dormant) -> 1100, ranks last.
        assertEquals(1100, TaskCalculator.priorityScore(ThreatTier.TITAN, MonsterState.DORMANT))
    }
}
