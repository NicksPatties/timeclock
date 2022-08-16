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

    @Test
    fun onSecondsValueChanged_cursorIsAtEndIfTextIsEmpty() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true
        )
        val fakeInput = TextFieldValue(text = "")
        testState.onSecondsValueChanged(fakeInput)
        assertThat(testState.secondsTextFieldValue).isEqualTo(
            TextFieldValue(
                text = "",
                selection = TextRange(0)
            )
        )
    }

    @Test
    fun onSecondsValueChanged_cursorIsAtEndIfTextIsADigitLessThanSix() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true
        )
        val fakeText = "1"
        val fakeInput = TextFieldValue(text = fakeText)
        testState.onSecondsValueChanged(fakeInput)
        assertThat(testState.secondsTextFieldValue).isEqualTo(
            TextFieldValue(
                text = fakeText,
                selection = TextRange(1)
            )
        )
    }

    @Test
    fun onSecondsValueChanged_selectedAllIfTextIsADigitGreaterThanSix() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true
        )
        val fakeText = "6"
        val fakeInput = TextFieldValue(text = fakeText)
        testState.onSecondsValueChanged(fakeInput)
        assertThat(testState.secondsTextFieldValue).isEqualTo(
            TextFieldValue(
                text = fakeText,
                selection = TextRange(0,1)
            )
        )
    }

    @Test
    fun onSecondsValueChanged_selectedAllIfTextIsTwoDigitsLong() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true
        )
        val fakeText = "16"
        val fakeInput = TextFieldValue(text = fakeText)
        testState.onSecondsValueChanged(fakeInput)
        assertThat(testState.secondsTextFieldValue).isEqualTo(
            TextFieldValue(
                text = fakeText,
                selection = TextRange(0,2)
            )
        )
    }

    @Test
    fun onHoursValueChanged_cursorAtEndIfNoDigits() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true
        )
        val fakeText = ""
        val fakeInput = TextFieldValue(text = fakeText)
        testState.onSecondsValueChanged(fakeInput)
        assertThat(testState.secondsTextFieldValue).isEqualTo(
            TextFieldValue(
                text = fakeText,
                selection = TextRange(0)
            )
        )
    }

    @Test
    fun onHoursValueChanged_cursorAtEndIfOneDigit() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true
        )
        val fakeText = "1"
        val fakeInput = TextFieldValue(text = fakeText)
        testState.onSecondsValueChanged(fakeInput)
        assertThat(testState.secondsTextFieldValue).isEqualTo(
            TextFieldValue(
                text = fakeText,
                selection = TextRange(1)
            )
        )
    }

    @Test
    fun onHoursValueChanged_selectedAllIfTwoDigits() {
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = true
        )
        val fakeText = "11"
        val fakeInput = TextFieldValue(text = fakeText)
        testState.onSecondsValueChanged(fakeInput)
        assertThat(testState.secondsTextFieldValue).isEqualTo(
            TextFieldValue(
                text = fakeText,
                selection = TextRange(0, 2)
            )
        )
    }

    @Test
    fun confirmBatteryWarningDialog_callsStartBatteryManagementActivityFunction() {
        var counter = 0
        val testState = ClockPageViewModelState(
            batteryWarningDialogVisible = true,
            startBatteryManagementActivity = {
                counter++
            }
        )
        testState.confirmBatteryWarningDialog()
        assertThat(counter).isEqualTo(1)
        assertThat(testState.batteryWarningDialogVisible).isFalse()
    }

    @Test
    fun dismissBatteryWarningDialog_dialogNotVisibleAfterDismiss() {
        val testState = ClockPageViewModelState(
            batteryWarningDialogVisible = true
        )
        testState.dismissBatteryWarningDialog()
        assertThat(testState.batteryWarningDialogVisible).isFalse()
    }

    @Test
    fun onTaskTextFieldIconClick_switchesCountDownTimerEnabled() {
        var counter = 0
        val testState = ClockPageViewModelState(
            countDownTimerEnabled = false,
            saveCountDownTimerEnabledValue = { counter++ }
        )
        testState.onTaskTextFieldIconClick()
        assertThat(testState.countDownTimerEnabled).isTrue()
        assertThat(counter).isEqualTo(1) // saveCountDownTimerEnabled has been called
    }

    @Test
    fun onTaskTextFieldIconClick_shouldWarnIfBatterySettingsNotOptimized() {
        val testState = ClockPageViewModelState(
            checkBatteryOptimizationSettings = { true },
            countDownTimerEnabled = false
        )
        testState.onTaskTextFieldIconClick()
        assertThat(testState.countDownTimerEnabled).isFalse()
        assertThat(testState.batteryWarningDialogVisible).isTrue()
    }

    @Test
    fun onClockStart_worksAsIntended() {
        var saveOnStartCounter = 0
        var saveOnStopCounter = 0
        val testState = ClockPageViewModelState(
            saveEventDataOnStart = { saveOnStartCounter++ },
            saveEventDataOnStop = { saveOnStopCounter++ },
            currSeconds = 10
        )
        testState.onClockStart()
        assertThat(testState.isClockRunning).isTrue()
        assertThat(testState.currSeconds).isEqualTo(0)
        assertThat(saveOnStartCounter).isEqualTo(1)
        assertThat(saveOnStopCounter).isEqualTo(0)
    }

    @Test
    fun onClockStop_worksAsIntended() {
        var saveOnStartCounter = 0
        var saveOnStopCounter = 0
        val testState = ClockPageViewModelState(
            saveEventDataOnStart = { saveOnStartCounter++ },
            saveEventDataOnStop = { saveOnStopCounter++ }
        )
        testState.onClockStop()
        assertThat(testState.isClockRunning).isFalse()
        assertThat(saveOnStartCounter).isEqualTo(0)
        assertThat(saveOnStopCounter).isEqualTo(1)
    }
}
