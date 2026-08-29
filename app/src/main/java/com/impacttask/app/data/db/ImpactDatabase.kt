package com.impacttask.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.impacttask.app.data.db.entity.ExpLedgerEntity
import com.impacttask.app.data.db.entity.GainEntity
import com.impacttask.app.data.db.entity.SubtaskEntity
import com.impacttask.app.data.db.entity.TaskEntity
import com.impacttask.app.data.db.entity.TaskGainEntity

@Database(
    entities = [
        GainEntity::class,
        TaskEntity::class,
        SubtaskEntity::class,
        TaskGainEntity::class,
        ExpLedgerEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ImpactDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun gainDao(): GainDao
    abstract fun expLedgerDao(): ExpLedgerDao
}
