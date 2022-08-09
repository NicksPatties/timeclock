package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.nickspatties.timeclock.ui.components.*
import com.nickspatties.timeclock.ui.viewmodel.ClockPageViewModelState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockPage(
    viewModelState: ClockPageViewModelState
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (viewModelState.batteryWarningDialogVisible) {
        BatteryWarningDialog(
            confirmFunction = viewModelState::confirmBatteryWarningDialog,
            dismissFunction = viewModelState::dismissBatteryWarningDialog
        )
    }

    Scaffold() {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                // Task name input
                // todo character limit 120
                val widthFraction = 0.9f
                TaskTextField(
                    modifier = Modifier
                        .testTag("TaskTextField")
                        .fillMaxWidth(widthFraction),
                    value = viewModelState.taskTextFieldValue,
                    enabled = !viewModelState.isClockRunning,
                    onTaskNameChange = viewModelState::onTaskNameChange,
                    onDone = {
                        if (viewModelState.countDownTimerEnabled) {
                            focusManager.moveFocus(FocusDirection.Next)
                        } else {
                            focusManager.clearFocus()
                        }
                        viewModelState.dismissDropdown()
                    },
                    keyboardController = keyboardController,
                    countdownTimerEnabled = viewModelState.countDownTimerEnabled,
                    onIconClick = viewModelState.onTaskNameIconClick
                )

                DropdownMenu(
                    modifier = Modifier
                        // 144 = 3 * 48 (the default height of a DropdownMenuItem
                        .requiredSizeIn(maxHeight = 144.dp)
                        .fillMaxWidth(widthFraction),
                    expanded = viewModelState.dropdownExpanded,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = viewModelState::dismissDropdown,
                ) {
                    viewModelState.filteredEventNames.forEach { label ->
                        DropdownMenuItem(
                            modifier = Modifier.testTag("DropdownMenuItem_${label}"),
                            onClick = {
                                if (viewModelState.countDownTimerEnabled) {
                                    focusManager.moveFocus(FocusDirection.Next)
                                } else {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                                viewModelState.onDropdownMenuItemClick(label)
                            }
                        ) {
                            Text(text = label)
                        }
                    }
                }
            }

            // timer clock
            val spacing = 0.dp
            if (viewModelState.countDownTimerEnabled) {
                EditTimerTextField(
                    hoursTextFieldValue = viewModelState.hoursTextFieldValue,
                    minutesTextFieldValue = viewModelState.minutesTextFieldValue,
                    secondsTextFieldValue = viewModelState.secondsTextFieldValue,
                    clickable = !viewModelState.isClockRunning,
                    onHoursValueChanged = viewModelState::onHoursValueChanged,
                    onMinutesValueChanged = viewModelState::onMinutesValueChanged,
                    onSecondsValueChanged = viewModelState::onSecondsValueChanged,
                    onHoursFocusChanged = viewModelState::onHoursFocusChanged,
                    onMinutesFocusChanged = viewModelState::onMinutesFocusChanged,
                    onSecondsFocusChanged = viewModelState::onSecondsFocusChanged,
                )
            } else {
                TimerText(
                    modifier = Modifier.padding(
                        top = spacing,
                        bottom = spacing
                    ),
                    isRunning = viewModelState.isClockRunning,
                    currSeconds = viewModelState.currSeconds,
                    finishedListener = viewModelState.onTimerAnimationFinish
                )
            }

            StartTimerButton(
                modifier = Modifier.testTag("StartTimerButton"),
                clockEnabled = viewModelState.clockButtonEnabled,
                isRunning = viewModelState.isClockRunning,
                startClock = viewModelState.onClockStart,
                stopClock = viewModelState.onClockStop
            )
        }
    }
}

@Composable
@Preview
fun ClockPage_Initial() {
    ClockPage(
        viewModelState = ClockPageViewModelState()
    )
}

@Composable
@Preview
fun ClockPage_batteryDialogVisible() {
    ClockPage(
        viewModelState = ClockPageViewModelState(
            batteryWarningDialogVisible = true
        )
    )
}