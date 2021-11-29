package com.nickspatties.timeclock.util

import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class ClockPageUtilTest {

    @Test
    @Parameters(
        "0,1000,1000",
        "0,1001,999",
        "0,999,1"
    )
    fun findEventStartTimeDelay_returnsCorrectValue(
        startTime: Long,
        currentTime: Long,
        expectedDelay: Long
    ) {
        assertThat(findEventStartTimeDelay(startTime, currentTime = currentTime)).isEqualTo(
            expectedDelay
        )
    }
}