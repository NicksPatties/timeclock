package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.nickspatties.timeclock.ui.components.EditTimerTextField
import com.nickspatties.timeclock.ui.components.StartTimerButton
import com.nickspatties.timeclock.ui.components.TaskTextField
import com.nickspatties.timeclock.ui.components.TimerText
import com.nickspatties.timeclock.ui.viewmodel.ClockPageViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockPage(
    viewModel: ClockPageViewModel
) {
    // moving variables from the main activity to in here
    val clockEnabled = viewModel.clockButtonEnabled
    val isRunning = viewModel.isClockRunning
    val dropdownExpanded = viewModel.dropdownExpanded
    // observe changes on autofillTaskNames to allow filteredTaskNames to function properly
    viewModel.autofillTaskNames.observeAsState()
    val filteredTaskNames = viewModel.filteredEventNames
    val taskTextFieldValue = viewModel.taskTextFieldValue
    val currSeconds = viewModel.currSeconds
    val onTaskNameChange = viewModel::onTaskNameChange
    val onTaskNameDonePressed = viewModel::onTaskNameDonePressed
    val onDismissDropdown = viewModel::onDismissDropdown
    val onDropdownMenuItemClick = viewModel::onDropdownMenuItemClick
    val startClock = viewModel::startClock
    val stopClock = viewModel::stopClock
    val onTimerAnimationFinished = viewModel::resetCurrSeconds
    val countdownEnabled = viewModel.countDownTimerEnabled
    val onCountdownIconClicked = viewModel::switchCountDownTimer
    val hoursTextFieldValue = viewModel.hoursTextFieldValue
    val minutesTextFieldValue = viewModel.minutesTextFieldValue
    val secondsTextFieldValue = viewModel.secondsTextFieldValue
    val batteryWarningDialogVisible = viewModel.batteryWarningDialogVisible

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

//    if (batteryWarningDialogVisible) {
//        batteryWarningDialog(
//            confirmFunction = { viewModel.goToBatterySettings() },
//            dismissFunction = { viewModel.hideBatteryWarningModal() }
//        )
//    }

    Scaffold() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                // Task name input
                // todo character limit 120
                val widthFraction = 0.9f
                TaskTextField(
                    modifier = Modifier
                        .fillMaxWidth(widthFraction),
                    value = taskTextFieldValue,
                    enabled = !isRunning,
                    onTaskNameChange = {
                        onTaskNameChange(it)
                    },
                    onDone = {
                        if (countdownEnabled) {
                            focusManager.moveFocus(FocusDirection.Next)
                        } else {
                            focusManager.clearFocus()
                        }
                        onTaskNameDonePressed()
                    },
                    keyboardController = keyboardController,
                    countdownTimerEnabled = countdownEnabled,
                    onIconClick = onCountdownIconClicked
                )

                DropdownMenu(
                    modifier = Modifier
                        // 144 = 3 * 48 (the default height of a DropdownMenuItem
                        .requiredSizeIn(maxHeight = 144.dp)
                        .fillMaxWidth(widthFraction),
                    expanded = dropdownExpanded,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = {
                        onDismissDropdown()
                    },
                ) {
                    filteredTaskNames.forEach { label ->
                        DropdownMenuItem(onClick = {
                            if (countdownEnabled) {
                                focusManager.moveFocus(FocusDirection.Next)
                            } else {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                            onDropdownMenuItemClick(label)
                        }) {
                            Text(text = label)
                        }
                    }
                }
            }

            // timer clock
            val spacing = 0.dp
            if (countdownEnabled) {
                EditTimerTextField(
                    viewModel = viewModel,
                    hoursTextFieldValue = hoursTextFieldValue,
                    minutesTextFieldValue = minutesTextFieldValue,
                    secondsTextFieldValue = secondsTextFieldValue,
                    clickable = !isRunning,
                    focusManager = focusManager
                )
            } else {
                TimerText(
                    modifier = Modifier.padding(
                        top = spacing,
                        bottom = spacing
                    ),
                    isRunning = isRunning,
                    currSeconds = currSeconds,
                    finishedListener = onTimerAnimationFinished
                )
            }

            StartTimerButton(
                clockEnabled = clockEnabled,
                isRunning = isRunning,
                startClock = startClock,
                stopClock = stopClock
            )
        }
    }
}

@Composable
@Preview
fun batteryWarningDialog(
    confirmFunction: () -> Unit = {},
    dismissFunction: () -> Unit = {}
) {
    AlertDialog(
        modifier = Modifier,
        onDismissRequest = dismissFunction,
        title = { Text("Hey guess what?") },
        text = { Text("So there's some stuff that you gotta do with the battery settings and stuff? Yeah, that's pretty annoying.\n\nDo you mind changing those things? Thanks.")},
        confirmButton = { TextButton(onClick = confirmFunction) {
            Text(text = "Confirm".uppercase())
        }},
        dismissButton = { TextButton(onClick = dismissFunction) {
            Text("Deny".uppercase())
        }}
    )
}

@Composable
@Preview
fun ClockPageMockUp() {
//    val defaultTextFieldValue = TextFieldValue("00")
//    ClockPage(
//        clockEnabled = false,
//        isRunning = false,
//        dropdownExpanded = false,
//        taskTextFieldValue = TextFieldValue(),
//        currSeconds = 0,
//        onTaskNameChange = { },
//        onTaskNameDonePressed = { },
//        onDismissDropdown = { },
//        onDropdownMenuItemClick = { },
//        startClock = { },
//        stopClock = { },
//        onCountdownIconClicked = {},
//        hoursTextFieldValue = defaultTextFieldValue,
//        minutesTextFieldValue = defaultTextFieldValue,
//        secondsTextFieldValue = defaultTextFieldValue,
//        viewModel = null
//    )
}