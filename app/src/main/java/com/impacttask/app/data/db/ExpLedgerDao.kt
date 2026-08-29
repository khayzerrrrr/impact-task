package com.impacttask.app.data.db

import androidx.room.Insert
import androidx.room.Query
import com.impacttask.app.data.db.entity.ExpLedgerEntity
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface ExpLedgerDao {
    @Insert
    suspend fun insert(entry: ExpLedgerEntity)

    @Query("SELECT * FROM exp_ledger WHERE ownerId = :ownerId ORDER BY occurredAt DESC")
    fun observeLedger(ownerId: String): Flow<List<ExpLedgerEntity>>
}
