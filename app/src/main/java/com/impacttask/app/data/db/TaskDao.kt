package com.impacttask.app.data.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.impacttask.app.data.db.entity.SubtaskEntity
import com.impacttask.app.data.db.entity.TaskEntity
import com.impacttask.app.data.db.entity.TaskGainEntity
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface TaskDao {
    @Transaction
    @Query("SELECT * FROM tasks WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun observeTasks(ownerId: String): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun observeTask(taskId: String): Flow<TaskWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllocations(allocations: List<TaskGainEntity>)

    @Query("DELETE FROM task_gains WHERE taskId = :taskId")
    suspend fun clearAllocations(taskId: String)

    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId")
    suspend fun countSubtasks(taskId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSubtask(subtask: SubtaskEntity)

    @Delete
    suspend fun deleteSubtask(subtask: SubtaskEntity)
}
