package com.nickspatties.timeclock.util

import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.ui.viewmodel.ListRow

/**
 * Creates a list of TimeClockEvents. Useful for testing and filling in mock data. Note that the
 * list will be reversed to simulate sorting in descending date order, just like in the data layer.
 *
 * @param eventCount The number of events to create in the list. Default is 5.
 * @param eventNames The names of the events to appear in the list. Default is "Programming", "Reading", "Journaling."
 * @param eventDurations The durations of the events in milliseconds. Default is 2 hours.
 * @param durationsBetween The duration of time in between events in milliseconds,
 *   starting with the time between the first and second event. Default is 1 hour.
 *
 * @see com.nickspatties.timeclock.util.convertHoursMinutesSecondsToMillis
 */
fun createMockTimeClockEventList(
    eventCount: Int = 5,
    eventNames: List<String> = listOf("Programming", "Reading", "Journaling"),
    eventDurations: List<Long> = listOf(2 * MILLIS_PER_HOUR),
    durationsBetween: List<Long> = listOf(MILLIS_PER_HOUR),
): List<TimeClockEvent> {
    val eventList = mutableListOf<TimeClockEvent>()
    var startTime = 0L
    for(i in 0 until eventCount) {
        val endTime = startTime + eventDurations[i % eventDurations.size]
        eventList += TimeClockEvent(
            eventNames[i % eventNames.size],
            startTime,
            endTime
        )
        startTime = endTime + durationsBetween[i % durationsBetween.size] // each event will be an hour apart from each other
    }
    return eventList.reversed()
}

val MockTimeClockEvents = createMockTimeClockEventList()
val MockTimeClockEventsGroupedByDate: Map<String, List<ListRow>> =
    groupEventsByDate(MockTimeClockEvents)
val MockAutofillValues = getAutofillValues(MockTimeClockEvents)