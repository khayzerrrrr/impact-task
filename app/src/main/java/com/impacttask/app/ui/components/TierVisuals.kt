package com.impacttask.app.ui.components

import com.impacttask.ThreatTier

/** 1-10 display number -- ThreatTier's enum ordinal is 0-9. */
fun ThreatTier.number(): Int = ordinal + 1
