package com.impacttask.app.ui.components

import androidx.compose.ui.graphics.Color
import com.impacttask.MonsterState
import com.impacttask.app.ui.theme.Amber
import com.impacttask.app.ui.theme.Blue
import com.impacttask.app.ui.theme.Cyan
import com.impacttask.app.ui.theme.Rose
import com.impacttask.app.ui.theme.Violet

fun MonsterState.label(): String = when (this) {
    MonsterState.DORMANT -> "Tidur"
    MonsterState.STIRRING -> "Menggeliat"
    MonsterState.AWAKE -> "Bangun"
    MonsterState.RAMPAGE -> "Mengamuk"
    MonsterState.FERAL -> "Liar"
}

fun MonsterState.tone(): Color = when (this) {
    MonsterState.DORMANT -> Blue
    MonsterState.STIRRING -> Amber
    MonsterState.AWAKE -> Cyan
    MonsterState.RAMPAGE -> Rose
    MonsterState.FERAL -> Violet
}
