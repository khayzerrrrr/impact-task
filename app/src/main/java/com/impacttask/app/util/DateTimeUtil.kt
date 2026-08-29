package com.impacttask.app.util

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToLong

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime.toEpochMillis(): Long =
    atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

/** Short human label for a deadline, e.g. "3 jam lagi" / "Terlewat 2 hari". */
fun formatDue(dueAt: LocalDateTime?, now: LocalDateTime = LocalDateTime.now()): String {
    if (dueAt == null) return "Tanpa tenggat"
    val hours = Duration.between(now, dueAt).toMillis() / 3_600_000.0
    return when {
        hours < 0 -> {
            val past = -hours
            if (past < 24) "Terlewat ${past.roundToLong()} jam" else "Terlewat ${(past / 24).roundToLong()} hari"
        }
        hours < 24 -> "${hours.roundToLong()} jam lagi"
        else -> "${(hours / 24).roundToLong()} hari lagi"
    }
}
