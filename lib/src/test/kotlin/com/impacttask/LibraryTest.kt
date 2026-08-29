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
    fun `gain balancing bonus rewards the lowest progress gain`() {
        val multiplier = TaskCalculator.balanceMultiplier(
            gainLevels = mapOf(
                Gain.RAGA to 12,
                Gain.NALAR to 8,
                Gain.KARYA to 18,
                Gain.HARTA to 9,
                Gain.IKATAN to 7,
                Gain.JIWA to 14,
            )
        )

        assertTrue(multiplier > 1.0)
        assertEquals(1.15, multiplier, 0.0001)
    }
}
