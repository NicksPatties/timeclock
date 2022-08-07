package com.nickspatties.timeclock.ui.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.google.common.truth.Truth.assertThat
import com.nickspatties.timeclock.util.MILLIS_PER_SECOND
import org.junit.Test

class ClockPageViewModelTest {

    @Test
    fun getCountDownSeconds_doesNotReturnNegativeNumber() {
        val pastEndTime = System.currentTimeMillis() - MILLIS_PER_SECOND
        val actualSeconds = getCountDownSeconds(pastEndTime)
        assertThat(actualSeconds).isEqualTo(0)
    }
}

class ClockPageViewModelStateTest {

    @Test
    fun clockButtonEnabled_returnsFalseIfCountDownDisabledAndTaskTextIsBlank() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = false,
            taskTextFieldValue = TextFieldValue("")
        )
        assertThat(testState.clockButtonEnabled).isFalse()
    }

    @Test
    fun clockButtonEnabled_returnsTrueIfCountDownDisabledAndTaskTextIsNotBlank() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = false,
            taskTextFieldValue = TextFieldValue("task name")
        )
        assertThat(testState.clockButtonEnabled).isTrue()
    }

    @Test
    fun clockButtonEnabled_returnsFalseIfCountDownEnabledTaskTextIsBlankAndTimerIsZero() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true,
            taskTextFieldValue = TextFieldValue(""),
            hoursTextFieldValue = TextFieldValue("00"),
            minutesTextFieldValue = TextFieldValue("00"),
            secondsTextFieldValue = TextFieldValue("00")
        )
        assertThat(testState.clockButtonEnabled).isFalse()
    }

    @Test
    fun clockButtonEnabled_returnsFalseIfCountDownEnabledTaskTextIsNotBlankAndTimerIsZero() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true,
            taskTextFieldValue = TextFieldValue("task name"),
            hoursTextFieldValue = TextFieldValue("00"),
            minutesTextFieldValue = TextFieldValue("00"),
            secondsTextFieldValue = TextFieldValue("00")
        )
        assertThat(testState.clockButtonEnabled).isFalse()
    }

    @Test
    fun clockButtonEnabled_returnsFalseIfCountDownEnabledTaskTextIsBlankAndTimerIsNotZero() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true,
            taskTextFieldValue = TextFieldValue(""),
            hoursTextFieldValue = TextFieldValue("00"),
            minutesTextFieldValue = TextFieldValue("01"),
            secondsTextFieldValue = TextFieldValue("00")
        )
        assertThat(testState.clockButtonEnabled).isFalse()
    }

    @Test
    fun clockButtonEnabled_returnsTrueIfCountDownEnabledTaskTextIsNotBlankAndTimerIsNotZero() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true,
            taskTextFieldValue = TextFieldValue("task name"),
            hoursTextFieldValue = TextFieldValue("00"),
            minutesTextFieldValue = TextFieldValue("01"),
            secondsTextFieldValue = TextFieldValue("00")
        )
        assertThat(testState.clockButtonEnabled).isTrue()
    }

    @Test
    fun taskDropdown_showTaskNameDropdownMenuWhenTaskNameChangedAndFilteredNameIsPresent() {
        val testState = ClockPageViewModelState(
            autofillTaskNames = setOf("programming", "reading")
        )
        testState.onTaskNameChange(TextFieldValue("p"))
        assertThat(testState.dropdownExpanded).isTrue()
        assertThat(testState.filteredEventNames).isEqualTo(listOf("programming"))
    }

    @Test
    fun taskDropdown_doNotShowTaskNameDropdownMenuWhenTaskNameChangedAndFilteredNamesIsEmpty() {
        val testState = ClockPageViewModelState(
            autofillTaskNames = setOf("programming", "reading")
        )
        testState.onTaskNameChange(TextFieldValue("q"))
        assertThat(testState.dropdownExpanded).isFalse()
        assertThat(testState.filteredEventNames).isEqualTo(listOf<String>())
    }

    @Test
    fun taskDropdown_hideDropdownAndFillTaskTextFieldWithSelectionOnDropdownMenuItemClick() {
        val taskName = "programming"
        val testState = ClockPageViewModelState(
            taskTextFieldValue = TextFieldValue("pr"),
            autofillTaskNames = setOf(taskName, "reading"),
            dropdownExpanded = true
        )
        testState.onDropdownMenuItemClick(taskName)
        assertThat(testState.dropdownExpanded).isFalse()
        assertThat(testState.taskTextFieldValue).isEqualTo(
            TextFieldValue(
                text = taskName,
                selection = TextRange(taskName.length) // selection at end of string
            )
        )
    }

    @Test
    fun taskDropdown_hideDropdownWhenDismissed() {
        val testState = ClockPageViewModelState(
            taskTextFieldValue = TextFieldValue("pr"),
            autofillTaskNames = setOf("programming", "reading"),
            dropdownExpanded = true
        )
        testState.dismissDropdown()
        assertThat(testState.dropdownExpanded).isFalse()
    }
}