package com.nickspatties.timeclock.components

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.google.common.truth.Truth.assertThat
import com.nickspatties.timeclock.ui.components.formatDigitsAfterLeavingFocus
import com.nickspatties.timeclock.ui.components.onMinuteAndSecondValueChange
import org.junit.Test

class EditTimerTextFieldTest {

    @Test
    fun onMinuteAndSecondValueChange_allTextSelectedWhenDigitIsSixOrAbove() {
        val expectedValue = TextFieldValue(
            text = "6",
            selection = TextRange(0, 3)
        )
        val actualValue = onMinuteAndSecondValueChange(TextFieldValue(
            text = "6"
        ))

        assertThat(actualValue.text).isEqualTo(expectedValue.text)
        assertThat(actualValue.selection).isEqualTo(expectedValue.selection)
    }

    @Test
    fun onMinuteAndSecondValueChange_allTextSelectedWhenTwoDigits() {
        val expectedValue = TextFieldValue(
            text = "11",
            selection = TextRange(0, 3)
        )
        val actualValue = onMinuteAndSecondValueChange(TextFieldValue(
            text = "11"
        ))

        assertThat(actualValue.text).isEqualTo(expectedValue.text)
        assertThat(actualValue.selection).isEqualTo(expectedValue.selection)
    }

    @Test
    fun onHourValueChange_noTextSelectionWhenDigitIsAboveSix() {
        val expectedValue = TextFieldValue(
            text = "6",
            selection = TextRange(0, 1)
        )
        val actualValue = onMinuteAndSecondValueChange(TextFieldValue(
            text = "6"
        ))

        assertThat(actualValue.text).isEqualTo(expectedValue.text)
        assertThat(actualValue.selection).isEqualTo(expectedValue.selection)
    }

    @Test
    fun formatDigitsAfterLeavingFocus_happyCase() {
        assertThat(formatDigitsAfterLeavingFocus("1")).isEqualTo("01")
    }

    @Test
    fun formatDigitsAfterLeavingFocus_emptyCase() {
        assertThat(formatDigitsAfterLeavingFocus("")).isEqualTo("00")
    }

    @Test
    fun formatDigitsAfterLeavingFocus_twoDigitsCase() {
        assertThat(formatDigitsAfterLeavingFocus("11")).isEqualTo("11")
    }
}