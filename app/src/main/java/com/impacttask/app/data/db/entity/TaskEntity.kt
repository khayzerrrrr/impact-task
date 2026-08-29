package com.impacttask.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.data.model.TaskTimeType
import java.util.UUID

/**
 * Difficulty/impact/tier and their derived score are intentionally NOT stored
 * here -- tier is a pure function of (difficulty, impact) computed on read via
 * [com.impacttask.ThreatTier.fromScore], so there is nothing to keep in sync.
 * [skorTerkunci] enforces prd.md section 02's anti-gaming rule: difficulty and
 * impact can only be edited before the first focus session starts.
 */
@Entity(tableName = "tasks", indices = [Index("ownerId"), Index("status")])
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ownerId: String,
    val title: String,
    val notes: String = "",
    val difficulty: Int,
    val impact: Int,
    val timeType: TaskTimeType,
    val dueAt: Long? = null,
    val estimasiMenit: Int? = null,
    val aktualMenit: Int? = null,
    val status: TaskStatus = TaskStatus.ACTIVE,
    val skorTerkunci: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val cancelledAt: Long? = null,
    val expAwarded: Int? = null,
    val updatedAt: Long = System.currentTimeMillis(),
)
