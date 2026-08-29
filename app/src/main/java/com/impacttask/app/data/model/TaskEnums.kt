package com.impacttask.app.data.model

/** Lifecycle of a task row. A task is never deleted on completion/cancellation
 * (see prd.md "Membatalkan task juga sah" and the Bestiary/history requirement) --
 * it's just re-flagged, so history always survives. */
enum class TaskStatus { ACTIVE, DONE, CANCELLED }

/** The three time-input shapes from prd.md section 08. */
enum class TaskTimeType { LENTUR, BERTENGGAT, TERJADWAL }
