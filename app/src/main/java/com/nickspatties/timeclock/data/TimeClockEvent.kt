package com.nickspatties.timeclock.data

import java.util.UUID

data class TimeClockEvent(
    val name: String,
    val startTime: Long,
    val endTime: Long,
    val id: UUID = UUID.randomUUID()
)