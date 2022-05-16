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

/**
 * Returns a list of names and total millis used in the Analysis screen. An additional long
 * is included as an id to help keep track of the selected item in the screen.
 *
 * @return A list of the task names, durations, and an id indicating it's been selected
 */
fun sortByNamesAndTotalMillis(events: List<TimeClockEvent>): List<Triple<String, Long, Long>> {
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
    val eventDurationList = eventNameAndDurationMap.toList().sortedByDescending { it.second }
    var i = -1L
    return eventDurationList.map { pair ->
        i++
        Triple(pair.first, pair.second, i)
    }
}
