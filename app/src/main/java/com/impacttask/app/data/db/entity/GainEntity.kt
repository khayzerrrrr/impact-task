package com.impacttask.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import com.impacttask.Gain

/**
 * One row per (owner, Gain) pair. [totalExp] is a cache derived from
 * [ExpLedgerEntity] rows -- the ledger is the source of truth (see prd.md
 * section 14: "ExpLedger itu wajib, bukan opsional"), this column just
 * avoids summing the ledger on every read.
 */
@Entity(tableName = "gains", primaryKeys = ["id", "ownerId"], indices = [Index("ownerId")])
data class GainEntity(
    val id: Gain,
    val ownerId: String,
    val totalExp: Int = 0,
)
