package com.impacttask.app.data.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.impacttask.app.data.db.entity.MonsterEntity

@androidx.room.Dao
interface MonsterDao {
    @Query("SELECT * FROM monsters WHERE taskId = :taskId")
    suspend fun get(taskId: String): MonsterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MonsterEntity)
}
