package com.shujushuo.tracking.sdk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)
//
    @Query("SELECT * FROM events ORDER BY xwhen ASC")
    suspend fun getAllEvents(): List<EventEntity>

    @Query("DELETE FROM events WHERE id IN (:ids)")
    suspend fun deleteEventsByIds(ids: List<Long>)
}
