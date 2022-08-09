package com.nickspatties.timeclock.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.google.common.truth.Truth.assertThat
import com.nickspatties.timeclock.data.TimeClockEvent
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

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
    fun calculateCurrSeconds_returnsCorrectValue() {
        val testEvent = TimeClockEvent(name = "event name", startTime = 0)
        val mockCurrTime = 1000L
        assertThat(calculateCurrSeconds(testEvent, mockCurrTime)).isEqualTo(1)
    }

    @Test
    @Parameters(
        "0,12:00 AM", // testing millis since epoch, which is in UTC
        "43200000,12:00 PM"
    )
    fun decorateMillisToTimeString_returnsCorrectValue(millis: Long, expectedString: String) {
        assertThat(decorateMillisToTimeString(millis, TimeZone.getTimeZone("UTC")))
            .isEqualTo(expectedString)
    }

    @Test
    fun selectAllValue_returnsSameTextButWithAllTextSelected() {
        val input = TextFieldValue(
            text = "1"
        )
        assertThat(selectAllValue(input)).isEqualTo(
            TextFieldValue(
                text = "1",
                selection = TextRange(0, 1)
            )
        )
    }

    @Test
    fun cursorAtEnd_returnsSameTextButWithCursorAtEnd() {
        val input = TextFieldValue(
            text = "1"
        )
        assertThat(cursorAtEnd(input)).isEqualTo(
            TextFieldValue(
                text = "1",
                selection = TextRange(1)
            )
        )
    }
}