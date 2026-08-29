package com.impacttask.app.ui.components

import androidx.compose.ui.graphics.Color
import com.impacttask.Gain
import com.impacttask.app.ui.theme.GainHarta
import com.impacttask.app.ui.theme.GainIkatan
import com.impacttask.app.ui.theme.GainJiwa
import com.impacttask.app.ui.theme.GainKarya
import com.impacttask.app.ui.theme.GainNalar
import com.impacttask.app.ui.theme.GainRaga

/** Static per-Gain display metadata that isn't part of the domain model
 * (the [lib] module's Gain enum only carries the label -- see prd.md 03). */
fun Gain.color(): Color = when (this) {
    Gain.RAGA -> GainRaga
    Gain.NALAR -> GainNalar
    Gain.KARYA -> GainKarya
    Gain.HARTA -> GainHarta
    Gain.IKATAN -> GainIkatan
    Gain.JIWA -> GainJiwa
}

fun Gain.subtitle(): String = when (this) {
    Gain.RAGA -> "Fisik & kesehatan"
    Gain.NALAR -> "Ilmu & mental"
    Gain.KARYA -> "Karier & keahlian"
    Gain.HARTA -> "Finansial"
    Gain.IKATAN -> "Relasi & keluarga"
    Gain.JIWA -> "Spiritual & makna"
}

fun Gain.glyph(): String = label.take(1)
