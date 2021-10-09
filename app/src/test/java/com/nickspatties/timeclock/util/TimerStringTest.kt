package com.nickspatties.timeclock.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TimerStringTest {

    @Test
    fun whenSecondsIs0_print000000() {
        val actual = getTimerString(0)
        val expected = "00:00:00"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun whenSecondsIs59_print000059() {
        val actual = getTimerString(59)
        val expected = "00:00:59"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun whenSecondsIs60_print000100() {
        val actual = getTimerString(60)
        val expected = "00:01:00"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun whenSecondsIs61_print000101() {
        val actual = getTimerString(61)
        val expected = "00:01:01"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun whenSecondsIs3599_print005959() {
        val actual = getTimerString(3599)
        val expected = "00:59:59"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun whenSecondsIs3600_print010000() {
        val actual = getTimerString(3600)
        val expected = "01:00:00"
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun whenSecondsIs3601_print010001() {
        val actual = getTimerString(3601)
        val expected = "01:00:01"
        assertThat(actual).isEqualTo(expected)
    }
}