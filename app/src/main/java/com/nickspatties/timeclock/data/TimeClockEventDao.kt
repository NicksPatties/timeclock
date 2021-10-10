package com.nickspatties.timeclock.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimeClockEventDao {

    @Insert
    suspend fun insert(event: TimeClockEvent)

    @Update
    suspend fun update(event: TimeClockEvent)

    @Query("SELECT * from time_clock_event_table WHERE id = :key")
    suspend fun get(key: Long): TimeClockEvent?

    @Query("DELETE from time_clock_event_table")
    suspend fun clear()

    @Query("SELECT * from time_clock_event_table ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentEvent(): TimeClockEvent?

    @Query("SELECT * FROM time_clock_event_table ORDER BY id DESC")
    fun getAllEvents(): LiveData<List<TimeClockEvent>>
}