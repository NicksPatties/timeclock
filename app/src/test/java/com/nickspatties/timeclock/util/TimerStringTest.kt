package com.nickspatties.timeclock.util

import com.google.common.truth.Truth.assertThat
import com.nickspatties.timeclock.data.TimeClockEvent
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class TimerStringTest {

    @Test
    @Parameters(
        "0,00:00:00",
        "59,00:00:59",
        "60,00:01:00",
        "61,00:01:01",
        "3599,00:59:59",
        "3600,01:00:00",
        "3601,01:00:01",
    )
    fun getTimerString_returnsCorrectValue(currentSeconds: Int, expectedString: String) {
        assertThat(getTimerString(currentSeconds)).isEqualTo(expectedString)
    }

    @Test
    @Parameters(
        "3600000,1,0,0"
    )
    fun convertMillisToHoursMillisSeconds_returnsCorrectValue(
        millis: Long,
        expectedHours: Int,
        expectedMinutes: Int,
        expectedSeconds: Int
    ) {
        assertThat(convertMillisToHoursMinutesSeconds(millis))
            .isEqualTo(Triple(expectedHours, expectedMinutes, expectedSeconds))
    }

    @Test
    fun calculateCurrSeconds_returnsCorrectValue() {
        val testEvent = TimeClockEvent(name = "event name", startTime = 0)
        val mockCurrTime = 1000L
        assertThat(calculateCurrSeconds(testEvent, mockCurrTime)).isEqualTo(1)
    }
}