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

    @Test
    fun createMockTimeClockEventList_usesParametersCorrectly() {
        val size = 2
        val eventNames = listOf("Exercising", "Cooking")
        val exercisingDuration = convertHoursMinutesSecondsToMillis(1, 0, 0)
        val cookingDuration = convertHoursMinutesSecondsToMillis(0, 45, 0)
        val eventDurations = listOf(
            exercisingDuration,
            cookingDuration
        )
        val durationBetween = convertHoursMinutesSecondsToMillis(2)
        val durationsBetween = listOf(durationBetween)

        val events = createMockTimeClockEventList(
            eventCount = size,
            eventNames = eventNames,
            eventDurations = eventDurations,
            durationsBetween = durationsBetween
        )

        val cookingEvent = events[0]
        val exercisingEvent = events[1]

        // assert names have been assigned properly
        assertThat(cookingEvent.name).isEqualTo("Cooking")
        assertThat(exercisingEvent.name).isEqualTo("Exercising")

        // assert durations have been assigned properly
        assertThat(cookingEvent.endTime - cookingEvent.startTime)
            .isEqualTo(cookingDuration)
        assertThat(exercisingEvent.endTime - exercisingEvent.startTime)
            .isEqualTo(exercisingDuration)

        // assert time between is assigned properly
        assertThat(cookingEvent.startTime - exercisingEvent.endTime).isEqualTo(durationBetween)
    }
}