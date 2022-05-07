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