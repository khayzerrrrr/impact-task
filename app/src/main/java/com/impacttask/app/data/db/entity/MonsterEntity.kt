package com.impacttask.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.impacttask.MonsterState

/**
 * Per-task notification bookkeeping (prd.md section 14's Monster table, trimmed
 * to what Fase 2's notification budget actually needs -- species/HP are derived
 * live from TaskEntity, not duplicated here).
 */
@Entity(
    tableName = "monsters",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MonsterEntity(
    @PrimaryKey val taskId: String,
    val lastNotifiedState: MonsterState? = null,
    val lastNotifiedAt: Long? = null,
    val disturbanceCount: Int = 0,
)
