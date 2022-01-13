package com.nickspatties.timeclock.util

import com.nickspatties.timeclock.data.TimeClockEvent

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
    return eventList.reversed()
}

fun groupEventsByDate(events: List<TimeClockEvent>): Map<String, List<TimeClockEvent>> {
    return events.groupBy {
        decorateMillisToDateString(it.startTime)
    }
}

val MockTimeClockEvents: List<TimeClockEvent> = createMockTimeClockEventList()
val MockTimeClockEventsGroupedByDate = groupEventsByDate(MockTimeClockEvents)