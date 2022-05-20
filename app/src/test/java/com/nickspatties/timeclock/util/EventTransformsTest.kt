package com.nickspatties.timeclock.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

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
            assertThat(item.name).isEqualTo(expectedPair.second)
        }
    }
}