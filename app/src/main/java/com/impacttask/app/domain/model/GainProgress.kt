package com.impacttask.app.domain.model

import com.impacttask.Gain
import com.impacttask.TaskCalculator

data class GainProgress(
    val gain: Gain,
    val totalExp: Int,
) {
    private val curve = TaskCalculator.levelProgress(totalExp)
    val level: Int get() = curve.level
    val floorExp: Int get() = curve.floorExp
    val ceilExp: Int get() = curve.ceilExp
    val progress: Double get() = curve.progress
}
