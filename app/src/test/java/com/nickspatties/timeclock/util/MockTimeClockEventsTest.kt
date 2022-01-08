package com.nickspatties.timeclock.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MockTimeClockEventsTest {

    @Test
    fun createMockTimeClockEventList_createsListOfEvents() {
        val expectedSize = 5
        val events = createMockTimeClockEventList(expectedSize)
        assertThat(expectedSize == events.size)

        var previousEndTime = -1L
        for(event in events) {
            assertThat(event.startTime < event.endTime)
            assertThat(previousEndTime < event.startTime)
            previousEndTime = event.endTime
        }
    }
}