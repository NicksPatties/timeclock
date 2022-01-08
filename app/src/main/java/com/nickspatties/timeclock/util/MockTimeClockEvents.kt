package com.nickspatties.timeclock.util

import androidx.lifecycle.LiveData
import com.nickspatties.timeclock.data.TimeClockEvent

const val MILLIS_PER_SECOND = 1000L
const val SECONDS_PER_MINUTE = 60L
const val MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE
const val MINUTES_PER_HOUR = 60L
const val MILLIS_PER_HOUR = MILLIS_PER_MINUTE * MINUTES_PER_HOUR

fun convertHoursMinutesAndSecondsToMillis(hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Long {
    return MILLIS_PER_HOUR * hours + MILLIS_PER_MINUTE * minutes + MILLIS_PER_SECOND * seconds
}

fun createMockTimeClockEventList(eventCount: Int = 5): List<TimeClockEvent> {
    val eventList = mutableListOf<TimeClockEvent>()
    var startTime = 0L
    val eventNames = listOf("Programming", "Reading", "Journaling")
    for(i in 0 until eventCount) {
        val endTime = startTime + 2 * MILLIS_PER_HOUR // each event is two hours long
        eventList += TimeClockEvent(
            eventNames[i % eventNames.size],
            startTime,
            endTime
        )
        startTime = endTime + MILLIS_PER_HOUR // each event will be an hour apart from each other
    }
    return eventList.toList()
}

val MockTimeClockEvents: List<TimeClockEvent> = createMockTimeClockEventList()