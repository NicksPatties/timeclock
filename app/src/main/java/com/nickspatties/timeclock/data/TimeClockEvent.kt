package com.nickspatties.timeclock.data

import android.os.SystemClock
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_clock_event_table")
data class TimeClockEvent(

    @ColumnInfo(name = "task_name")
    val name: String,

    @ColumnInfo(name = "start_time_millis")
    val startTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_millis")
    var endTime: Long = startTime,

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L
)