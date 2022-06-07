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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.nickspatties.timeclock.ui.components.EditTimerTextField
import com.nickspatties.timeclock.ui.components.StartTimerButton
import com.nickspatties.timeclock.ui.components.TaskTextField
import com.nickspatties.timeclock.ui.components.TimerText
import com.nickspatties.timeclock.util.MockAutofillValues

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClockPage(
    clockEnabled: Boolean,
    isRunning: Boolean,
    countdownEnabled: Boolean = false,
    dropdownExpanded: Boolean,
    taskTextFieldValue: TextFieldValue,
    autofillTaskNames: Set<String>?,
    currSeconds: Int,
    onTaskNameChange: (TextFieldValue) -> Unit,
    onTaskNameDonePressed: () -> Unit,
    onDismissDropdown: () -> Unit,
    onDropdownMenuItemClick: (String) -> Unit,
    startClock: () -> Unit,
    stopClock: () -> Unit,
    timerAnimationFinishedListener: () -> Unit = {},
    onCountdownIconClicked: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current

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
                    modifier = Modifier.fillMaxWidth(widthFraction),
                    value = taskTextFieldValue,
                    enabled = !isRunning,
                    onTaskNameChange = {
                        onTaskNameChange(it)
                    },
                    onDone = {
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
                    val filteredTaskNames = autofillTaskNames!!.filter {
                        it.contains(taskTextFieldValue.text)
                    }

                    if (filteredTaskNames.isNotEmpty()) {
                        filteredTaskNames.forEach { label ->
                            DropdownMenuItem(onClick = {
                                onDropdownMenuItemClick(label)
                                keyboardController?.hide() // close keyboard
                            }) {
                                Text(text = label)
                            }
                        }
                    }
                }
            }

            // timer clock
            val spacing = 0.dp
            if (countdownEnabled) {
                EditTimerTextField()
            } else {
                TimerText(
                    modifier = Modifier.padding(
                        top = spacing,
                        bottom = spacing
                    ),
                    isRunning = isRunning,
                    currSeconds = currSeconds,
                    finishedListener = timerAnimationFinishedListener
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
fun ClockPageMockUp() {
    ClockPage(
        clockEnabled = false,
        isRunning = false,
        dropdownExpanded = false,
        taskTextFieldValue = TextFieldValue(),
        autofillTaskNames = MockAutofillValues,
        currSeconds = 0,
        onTaskNameChange = { },
        onTaskNameDonePressed = { },
        onDismissDropdown = { },
        onDropdownMenuItemClick = { },
        startClock = { },
        stopClock = { }
    ) { }
}