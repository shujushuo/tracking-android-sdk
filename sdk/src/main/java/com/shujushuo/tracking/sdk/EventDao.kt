package com.shujushuo.tracking.sdk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    //
    @Query("SELECT * FROM events ORDER BY xwhen ASC")
    suspend fun getAllEvents(): List<EventEntity>

    @Query("SELECT count(1) FROM events ")
    suspend fun getCount(): Int


    @Query("DELETE FROM events WHERE id IN (:ids)")
    suspend fun deleteEventsByIds(ids: List<Long>)

    @Query(
        """
        DELETE FROM events 
        WHERE id IN (
            SELECT id FROM events 
            WHERE (SELECT COUNT(*) FROM events) > :maxCount
            ORDER BY id DESC
            LIMIT (SELECT COUNT(*) - :maxCount FROM events)
        )
    """
    )
    suspend fun deleteOldestEventsIfExceed(maxCount: Int)

    @Transaction // 确保插入和删除在一个事务中执行
    suspend fun insertWithLimit(event: EventEntity, maxCount: Int) {
        insert(event) // 插入事件
        deleteOldestEventsIfExceed(maxCount) // 删除多余的历史事件
    }
}
