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
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // observe changes on autofillTaskNames to allow filteredTaskNames to function properly
    viewModel.autofillTaskNames.observeAsState()

    if (viewModel.batteryWarningDialogVisible) {
        batteryWarningDialog(
            confirmFunction = { viewModel.goToBatterySettings() },
            dismissFunction = { viewModel.hideBatteryWarningModal() }
        )
    }

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
                    value = viewModel.taskTextFieldValue,
                    enabled = !viewModel.isClockRunning,
                    onTaskNameChange = {
                        (viewModel::onTaskNameChange)(it)
                    },
                    onDone = {
                        if (viewModel.countDownTimerEnabled) {
                            focusManager.moveFocus(FocusDirection.Next)
                        } else {
                            focusManager.clearFocus()
                        }
                        (viewModel::onTaskNameDonePressed)()
                    },
                    keyboardController = keyboardController,
                    countdownTimerEnabled = viewModel.countDownTimerEnabled,
                    onIconClick = viewModel::switchCountDownTimer
                )

                DropdownMenu(
                    modifier = Modifier
                        // 144 = 3 * 48 (the default height of a DropdownMenuItem
                        .requiredSizeIn(maxHeight = 144.dp)
                        .fillMaxWidth(widthFraction),
                    expanded = viewModel.dropdownExpanded,
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = {
                        (viewModel::onDismissDropdown)()
                    },
                ) {
                    viewModel.filteredEventNames.forEach { label ->
                        DropdownMenuItem(onClick = {
                            if (viewModel.countDownTimerEnabled) {
                                focusManager.moveFocus(FocusDirection.Next)
                            } else {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                            (viewModel::onDropdownMenuItemClick)(label)
                        }) {
                            Text(text = label)
                        }
                    }
                }
            }

            // timer clock
            val spacing = 0.dp
            if (viewModel.countDownTimerEnabled) {
                EditTimerTextField(
                    viewModel = viewModel,
                    hoursTextFieldValue = viewModel.hoursTextFieldValue,
                    minutesTextFieldValue = viewModel.minutesTextFieldValue,
                    secondsTextFieldValue = viewModel.secondsTextFieldValue,
                    clickable = !viewModel.isClockRunning,
                    focusManager = focusManager
                )
            } else {
                TimerText(
                    modifier = Modifier.padding(
                        top = spacing,
                        bottom = spacing
                    ),
                    isRunning = viewModel.isClockRunning,
                    currSeconds = viewModel.currSeconds,
                    finishedListener = viewModel::resetCurrSeconds
                )
            }

            StartTimerButton(
                clockEnabled = viewModel.clockButtonEnabled,
                isRunning = viewModel.isClockRunning,
                startClock = viewModel::startClock,
                stopClock = viewModel::stopClock
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
//        viewModel = null
//    )
}