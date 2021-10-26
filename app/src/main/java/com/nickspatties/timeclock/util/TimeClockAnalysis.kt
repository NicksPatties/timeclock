package com.nickspatties.timeclock.util

import com.nickspatties.timeclock.data.TimeClockEvent

fun sortTotalDurationByName(events: List<TimeClockEvent>): Map<String, Long> {
    val eventMap = mutableMapOf<String, Long>()

    events.forEach { event ->
        val duration = event.endTime - event.startTime
        val name = event.name
        if (eventMap[name] == null) eventMap[name] = 0L
        eventMap[name] = eventMap[name]!!.plus(duration)
    }

    // sort map by value
    return eventMap.toList()
        .sortedBy { (_, value) -> -value } // largest value first
        .toMap()
}