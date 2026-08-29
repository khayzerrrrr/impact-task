package com.impacttask.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.impacttask.Gain
import java.util.UUID

enum class ExpReason { TASK_COMPLETE, TASK_REVERSAL, DECAY }

/**
 * Append-only transaction log. Required per prd.md section 14: without it you
 * can't undo a completion, chart 30-day momentum, or fix a scoring bug without
 * corrupting people's totals. [GainEntity.totalExp] is just a cached sum of this.
 */
@Entity(
    tableName = "exp_ledger",
    indices = [Index("ownerId"), Index("gainId"), Index("taskId")],
)
data class ExpLedgerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val gainId: Gain,
    val taskId: String?,
    val amount: Int,
    val reason: ExpReason,
    val occurredAt: Long = System.currentTimeMillis(),
)
