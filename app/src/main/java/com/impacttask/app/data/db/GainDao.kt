package com.impacttask.app.data.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.impacttask.Gain
import com.impacttask.app.data.db.entity.GainEntity
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface GainDao {
    @Query("SELECT * FROM gains WHERE ownerId = :ownerId")
    fun observeGains(ownerId: String): Flow<List<GainEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(gains: List<GainEntity>)

    @Query("UPDATE gains SET totalExp = totalExp + :delta WHERE id = :gainId AND ownerId = :ownerId")
    suspend fun addExp(ownerId: String, gainId: Gain, delta: Int)
}
