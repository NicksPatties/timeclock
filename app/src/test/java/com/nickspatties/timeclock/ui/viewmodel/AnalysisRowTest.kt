package com.nickspatties.timeclock.ui.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AnalysisRowTest {

    @Test
    fun getPercentage_returnsCorrectPercentage() {
        val expectedPercentage = 100f
        val millis = 1000L
        val testRow = AnalysisRow("name", millis, 0L)
        val actual = testRow.getPercentage(totalMillis = millis)
        assertThat(actual).isEqualTo(expectedPercentage)
    }

    @Test
    fun getPercentage_handlesTotalMillisOfZeroOrLess() {
        val testRow = AnalysisRow("name", 0L, 0L)
        val actual = testRow.getPercentage(0L)
        assertThat(actual).isEqualTo(0f)
    }
}