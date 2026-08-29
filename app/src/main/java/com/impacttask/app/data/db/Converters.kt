package com.impacttask.app.data.db

import androidx.room.TypeConverter
import com.impacttask.Gain
import com.impacttask.MonsterState
import com.impacttask.app.data.db.entity.ExpReason
import com.impacttask.app.data.model.TaskStatus
import com.impacttask.app.data.model.TaskTimeType

class Converters {
    @TypeConverter
    fun gainToString(value: Gain): String = value.name

    @TypeConverter
    fun stringToGain(value: String): Gain = Gain.valueOf(value)

    @TypeConverter
    fun monsterStateToString(value: MonsterState?): String? = value?.name

    @TypeConverter
    fun stringToMonsterState(value: String?): MonsterState? = value?.let(MonsterState::valueOf)

    @TypeConverter
    fun statusToString(value: TaskStatus): String = value.name

    @TypeConverter
    fun stringToStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun timeTypeToString(value: TaskTimeType): String = value.name

    @TypeConverter
    fun stringToTimeType(value: String): TaskTimeType = TaskTimeType.valueOf(value)

    @TypeConverter
    fun reasonToString(value: ExpReason): String = value.name

    @TypeConverter
    fun stringToReason(value: String): ExpReason = ExpReason.valueOf(value)
}
