package com.nickspatties.timeclock.util

import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class TimeConversionUtilsTest {

    @Test
    @Parameters(
        "1,0,0,3600000"
    )
    fun convertHoursMinutesSecondsToMillis_returnsCorrectValue(
        hours: Int,
        minutes: Int,
        seconds: Int,
        expectedMillis: Long
    ) {
        assertThat(convertHoursMinutesSecondsToMillis(hours, minutes, seconds))
            .isEqualTo(expectedMillis)
    }

    @Test
    @Parameters(
        "3600000,1,0,0"
    )
    fun convertMillisToHoursMinutesSeconds_returnsCorrectValue(
        millis: Long,
        expectedHours: Int,
        expectedMinutes: Int,
        expectedSeconds: Int
    ) {
        assertThat(convertMillisToHoursMinutesSeconds(millis))
            .isEqualTo(Triple(expectedHours, expectedMinutes, expectedSeconds))
    }
}