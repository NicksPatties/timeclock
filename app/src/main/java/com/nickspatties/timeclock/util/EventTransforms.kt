package com.nickspatties.timeclock.util

import com.nickspatties.timeclock.data.TimeClockEvent

fun groupEventsByDate(events: List<TimeClockEvent>): Map<String, List<TimeClockEvent>> {
    return events.groupBy {
        decorateMillisToDateString(it.startTime)
    }
}

fun getAutofillValues(events: List<TimeClockEvent>): Set<String> {
    return events.map {
        it.name
    }.toSet()
}

fun sortByNamesAndTotalMillis(events: List<TimeClockEvent>): List<Pair<String, Long>> {
    val eventNameAndDurationMap : MutableMap<String, Long> = mutableMapOf()
    for (event in events) {
        val name = event.name
        val duration = event.endTime - event.startTime
        if (eventNameAndDurationMap[name] == null) {
            eventNameAndDurationMap[name] = duration
        } else {
            val currentDuration = eventNameAndDurationMap[name]!!
            eventNameAndDurationMap[name] = currentDuration + duration
        }
    }
    // return a list of events sorted by the durations
    return eventNameAndDurationMap.toList().sortedByDescending { it.second }
}