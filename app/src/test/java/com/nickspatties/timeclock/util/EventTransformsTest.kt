package com.nickspatties.timeclock.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*

class EventTransformsTest {

    @Test
    fun sortByNamesAndTotalMillis_returnsExpectedList() {
        val programmingDuration = convertHoursMinutesSecondsToMillis(1)
        val bikingDuration = convertHoursMinutesSecondsToMillis(1)
        val readingDuration = convertHoursMinutesSecondsToMillis(0, 30)

        val mockEvents = createMockTimeClockEventList(
            eventCount = 3,
            eventNames = listOf(
                "Programming",
                "Biking",
                "Reading"
            ),
            eventDurations = listOf(
                programmingDuration,
                bikingDuration,
                readingDuration
            )
        )

        val analysisPageList = sortByNamesAndTotalMillis(mockEvents)
        val expectedPairs = listOf(
            Pair("Biking", bikingDuration),
            Pair("Programming", programmingDuration),
            Pair("Reading", readingDuration)
        )

        for ((i, item) in analysisPageList.withIndex()) {
            val expectedPair = expectedPairs[i]
            // assert the names are the same
            assertThat(item.name).isEqualTo(expectedPair.first)
            // assert the duration in millis is the same
            assertThat(item.millis).isEqualTo(expectedPair.second)
        }
    }

    @Test
    fun filterEventsByNumberOfDays_onlyGetsEventsFromToday() {
        val previousMidnight = findPreviousMidnight()
        val events = createMockTimeClockEventList(
            eventCount = 3,
            eventDurations = listOf(1L),
            durationsBetween = listOf(
                previousMidnight - 2L,
                1000L,
            )
        )
        val filteredEvents = filterEventsByNumberOfDays(events = events, numberOfDays = 1)
        assertThat(filteredEvents.size).isEqualTo(1)
    }

    @Test
    fun findPreviousMidnight_findsThePreviousMidnight() {
        val expectedMidnight = Calendar.getInstance()
        expectedMidnight.set(Calendar.HOUR_OF_DAY, 0)
        expectedMidnight.set(Calendar.MINUTE, 0)
        expectedMidnight.set(Calendar.SECOND, 0)
        expectedMidnight.set(Calendar.MILLISECOND, 0)

        val previousMidnight = findPreviousMidnight()
        assertThat(previousMidnight).isEqualTo(expectedMidnight.timeInMillis)
    }
}